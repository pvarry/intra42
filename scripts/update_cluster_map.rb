require 'net/https'
require 'cgi'
require 'json'

OUTPUT_PATH = File.join(__dir__, '..', 'app', 'src', 'main', 'res', 'raw')

old_files = Dir.entries(OUTPUT_PATH)
               .select { |name| name.match(%r(cluster_map_campus_\d+\.json)) }
               .map{|i| File.join(OUTPUT_PATH, i)}
File.delete(*old_files) if old_files.any?



def get_pad(key)

  conn = Net::HTTP.new('notepad.pw', 443)
  conn.use_ssl = true
  conn.verify_mode = OpenSSL::SSL::VERIFY_NONE

  req = Net::HTTP::Get.new("/raw/#{key}")
  res = conn.request(req)
  
  matches = res.body.match(%r(<pre.*?>(?<json>.*)?</pre>))
  JSON.parse(CGI.unescapeHTML(matches[:json]))
end




masters = get_pad('q79vdcc1t')
maps = masters.map do |master|
  get_pad(master['key']) unless master["name"].nil?
end.compact

maps.each do |cluster_map|
  cluster_map['map'].each do |col| 
    col.each do |cel|
      cel['host'] = cluster_map['host_prefix'] + cel['host'] if !cel['host'].nil? && cel['host'] != 'TBD' && cel['host'] != 'null'
    end
  end unless cluster_map['map'].nil?
end

maps.group_by { |i| i['campus_id'] }.each do |campus_id, cluster_map|

  cluster_map.sort_by! { |l| l['position'] }
  File.write(File.join(OUTPUT_PATH, "cluster_map_campus_#{campus_id}.json"),  cluster_map.to_json)
end

