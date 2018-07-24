#!/usr/bin/ruby

require 'net/https'
require 'cgi'
require 'json'
require 'fileutils'

print 'init'

INTRA_42_SESSION_FILE = File.join(__dir__, '._intra_42_session_key')
API_42_TOKEN_FILE = File.join(__dir__, '._42_token_api')
OUTPUT_PATH = File.join(__dir__, '..', 'app', 'src', 'main', 'res', 'raw')

unless File.exist?(INTRA_42_SESSION_FILE)
  puts "File \"#{INTRA_42_SESSION_FILE}\" must contain intra's 42 cookie"
  exit(1)
end

unless File.exist?(API_42_TOKEN_FILE)
  puts "File \"#{API_42_TOKEN_FILE}\" must contain 42 api token"
  exit(1)
end

INTRA_42_SESSION_KEY = File.read(INTRA_42_SESSION_FILE)
API_42_TOKEN = File.read(API_42_TOKEN_FILE)

conn_api = Net::HTTP.new('api.intra.42.fr', 443)
conn_api.use_ssl = true
conn_api.verify_mode = OpenSSL::SSL::VERIFY_NONE

req_cursus = Net::HTTP::Get.new("/v2/cursus?sort=id")
req_cursus.add_field("Authorization",  'Bearer '+ API_42_TOKEN)
res_cursus = conn_api.request(req_cursus)

req_campus = Net::HTTP::Get.new("/v2/campus?sort=id")
req_campus.add_field("Authorization",  'Bearer '+ API_42_TOKEN)
res_campus = conn_api.request(req_campus)


# intra
conn_intra = Net::HTTP.new('projects.intra.42.fr', 443)
conn_intra.use_ssl = true
conn_intra.verify_mode = OpenSSL::SSL::VERIFY_NONE

req_intra = Net::HTTP::Get.new("/")
req_intra['Cookie'] = CGI::Cookie.new('_intra_42_session_production', INTRA_42_SESSION_KEY).to_s
intra_check = conn_intra.request(req_intra)

unless res_cursus.kind_of? Net::HTTPSuccess
  print "\n"
  puts 'API token error: ' +  res_cursus.body
  exit
end

if !intra_check.kind_of?(Net::HTTPSuccess) || intra_check.kind_of?(Net::HTTPRedirection)
  print "\n"
  puts 'Intra Cookie error: ' + intra_check.body
  exit
end

old_files = Dir.entries(OUTPUT_PATH)
               .select { |name| name.match(%r(project_data_cursus_\d+_campus_\d+\.json)) }
               .map{|i| File.join(OUTPUT_PATH, i)}
File.delete(*old_files) if old_files.any?

cursus = JSON.parse(res_cursus.body)
campus = JSON.parse(res_campus.body)
max = (cursus.length * campus.length).to_f

cursus.each_with_index do |cursus_item, cursus_index|
  campus.each_with_index do |campus_item, campus_index|

    print "\r"
    percentage = (campus.length * cursus_index + campus_index) / max
    p = (percentage * 20).round

    (0..p).each do
      print '▓'
    end

    (p..20).each do
      print '░'
    end

    print " " + (percentage * 100).round(2).to_s + "%"

    print " Calling cursus: #{cursus_item['id']}, campus: #{campus_item['id']} ..."

    req = Net::HTTP::Get.new("/project_data.json?cursus_id=#{cursus_item["id"]}&campus_id=#{campus_item["id"]}")
    req['Cookie'] = CGI::Cookie.new('_intra_42_session_production', INTRA_42_SESSION_KEY).to_s

    res = conn_intra.request(req)
    json = JSON.parse(res.body)
    next if json.length.zero?

    json.each do |x|
      x.delete("state")
      x.delete("final_mark")
    end

    sorted = json.sort_by { |k| k["id"] }

    File.write(File.join(OUTPUT_PATH, "project_data_cursus_#{cursus_item["id"]}_campus_#{campus_item["id"]}.json"), sorted.to_json)

  end
end

FileUtils.cp(File.join(OUTPUT_PATH, 'project_data_cursus_1_campus_1.json'), File.join(OUTPUT_PATH, 'project_data.json'))

print "\a"