require 'net/https'
require 'cgi'
require 'json'

INTRA_42_SESSION_FILE = File.join(__dir__, '._intra_42_session_key')
OUTPUT_PATH = File.join(__dir__, '..', 'app', 'src', 'main', 'res', 'raw')

unless File.exist?(INTRA_42_SESSION_FILE)
  puts "File \"#{INTRA_42_SESSION_FILE}\" must contain intra's 42 cookie"
  exit(1)
end

INTRA_42_SESSION_KEY = File.read(INTRA_42_SESSION_FILE)

conn = Net::HTTP.new('projects.intra.42.fr', 443)
conn.use_ssl = true
conn.verify_mode = OpenSSL::SSL::VERIFY_NONE

(1..16).each do |cursus_id|
  (1..9).each do |campus_id|

    print "Calling cursus: #{cursus_id}, campus: #{campus_id} ...  "

    req = Net::HTTP::Get.new("/project_data.json?cursus_id=#{cursus_id}&campus_id=#{campus_id}")
    req['Cookie'] = CGI::Cookie.new('_intra_42_session_production', INTRA_42_SESSION_KEY).to_s

    res = conn.request(req)

    if res.code != "200"
      print "\n"
      puts res.body
      puts "You need to sign in"
      exit
    end
    json = JSON(res.body)

    json.each do  |x|
      x["state"] = nil
      x["final_mark"] = nil
    end

    sorted = json.sort_by { |k| k["id"] }

    File.write(File.join(OUTPUT_PATH, "project_data_cursus_#{cursus_id}_campus_#{campus_id}.json"), sorted.to_json)

    puts "done"
  end
end