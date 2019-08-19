#!/usr/bin/ruby

require 'net/https'
require 'cgi'
require 'json'
require 'fileutils'

POEDITOR_API_FILE = File.join(__dir__, '._poeditor_api_key')
OUTPUT_PATH = File.join(__dir__, '..', 'app', 'src', 'main', 'res')

unless File.exist?(POEDITOR_API_FILE)
  puts "File \"#{POEDITOR_API_FILE}\" must contain PoEditor key"
  exit(1)
end

POEDITOR_API_KEY = File.read(POEDITOR_API_FILE)
POEDITOR_PROJET_ID = '133033'

conn = Net::HTTP.new('api.poeditor.com', 443)
conn.use_ssl = true
conn.verify_mode = OpenSSL::SSL::VERIFY_NONE

puts 'Initialize language list'
req = Net::HTTP::Post.new('/v2/languages/list')
req.body = URI.encode_www_form({ api_token: POEDITOR_API_KEY, id: POEDITOR_PROJET_ID })
res = conn.request(req)
json = JSON.parse(res.body)

languages = json['result']['languages'].reject { |language| language['translations'].zero? } # Ignore empty translation
                                       .map { |language| language['code'] }
languages.each do |data|
  language, country = data.split('-')
  local = country ? "#{language}-r#{country.upcase}" : language
  filename = 'values'
  filename += "-#{local}" if local != 'en' # Use english as default

  puts "Update language: #{local}"
  req = Net::HTTP::Post.new('/v2/projects/export')
  req.body = URI.encode_www_form({ api_token: POEDITOR_API_KEY, id: POEDITOR_PROJET_ID, language: language, type: 'android_strings', filters: 'translated' })
  res = conn.request(req)
  json = JSON.parse(res.body)

  req = Net::HTTP::Get.new(json['result']['url'])
  res = conn.request(req)
  language_path = File.join(OUTPUT_PATH, filename)
    
  FileUtils.mkdir_p(language_path)
  File.write(File.join(language_path, 'strings.xml'), res.body)
end