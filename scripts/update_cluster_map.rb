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
maps = masters.map { |master| get_pad(master['key']) }
              .map { |cluster_map|
                cluster_map['map']&.map! do |col|
                  col&.map! do |cel|
                    next if cel.nil?

                    if cel['host'] == 'TBD' || cel['host'] == 'null'
                      cel.delete('host')
                    elsif cel['host'] != nil
                      cel['host'] = cluster_map['host_prefix'] + cel['host']
                    end

                    cel
                  end
                end

                cluster_map
              }
              .sort_by { |cluster_map| cluster_map['position'] }
              .group_by { |cluster_map| cluster_map['campus_id'] }

maps.each do |campus_id, cluster_maps|
  File.write(File.join(OUTPUT_PATH, "cluster_map_campus_#{campus_id}.json"), cluster_maps.to_json)
end