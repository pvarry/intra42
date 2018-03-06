#!/usr/bin/ruby

require 'net/https'
require 'json'

OUTPUT_PATH = File.join(__dir__, '..', 'app', 'src', 'main', 'res', 'raw')

old_files = Dir.entries(OUTPUT_PATH)
               .select { |name| name.match(%r(cluster_map_campus_\d+\.json)) }
               .map{|i| File.join(OUTPUT_PATH, i)}
File.delete(*old_files) if old_files.any?



def get_pad(key)

  conn = Net::HTTP.new('jsonblob.com', 443)
  conn.use_ssl = true
  conn.verify_mode = OpenSSL::SSL::VERIFY_NONE

  req = Net::HTTP::Get.new("/api/jsonBlob/#{key}")
  res = conn.request(req)
  
  JSON.parse(res.body)
end




masters = get_pad('9d4791dc-1bd4-11e8-88aa-9d6752c34362')
maps = masters.map do |master|
  get_pad(master['key'])
end.compact

maps.each do |cluster_map|
  cluster_map['map'].each do |col| 
    col.each do |cel|
      if cel['host'] == 'TBD' || cel['host'] == 'null'
        cel.delete('host')
      elsif cel['host'] != nil
        cel['host'] = cluster_map['host_prefix'] + cel['host']
      end
    end
  end unless cluster_map['map'].nil?
end

maps.group_by { |i| i['campus_id'] }.each do |campus_id, cluster_map|

  cluster_map.sort_by! { |l| l['position'] }
  File.write(File.join(OUTPUT_PATH, "cluster_map_campus_#{campus_id}.json"),  cluster_map.to_json)
end

