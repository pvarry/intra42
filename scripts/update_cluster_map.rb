#!/usr/bin/ruby

require 'net/https'
require 'json'

OUTPUT_PATH = File.join(__dir__, '..', 'app', 'src', 'main', 'res', 'raw')

old_files = Dir.entries(OUTPUT_PATH)
               .select { |name| name.match(%r(cluster_map_campus_\d+\.json)) }
               .map{|i| File.join(OUTPUT_PATH, i)}
File.delete(*old_files) if old_files.any?

COLOR_RED = "\033[0;31m"
COLOR_GREEN = "\033[0;32m"
COLOR_YELLOW = "\033[1;33m"
COLOR_BLUE = "\033[0;34m"
COLOR_RESET = "\033[0m"

masters = JSON.parse(File.read("cluster_map-export.json", encoding: "iso-8859-1").force_encoding("UTF-8"))
maps = masters.map { |slug, cluster_map|

                if cluster_map.nil?
                  puts "#{COLOR_YELLOW}[not found]#{COLOR_RESET} ― #{cluster_map['name']} (#{slug})"
                  next
                end

                unless cluster_map['isReadyToPublish']
                  puts "#{COLOR_BLUE}[not ready]#{COLOR_RESET} ― #{cluster_map['name']} (#{slug})"
                  next
                end

                unless cluster_map['map'].is_a?(Array)
                  puts "#{COLOR_RED}[corrupted]#{COLOR_RESET} ― #{cluster_map['name']} (#{slug})"
                  next
                end

                puts "#{COLOR_GREEN}[good]#{COLOR_RESET}      ― #{cluster_map['name']} (#{slug})"

                cluster_map
              }
              .reject { |cluster_map| cluster_map.nil? }
              .map { |cluster_map|
                cluster_map['map']&.map! do |col|

                  if col.is_a?(Hash)
                    col = (0..(col.keys.last.to_i)).map do |i|
                      col[i.to_s]
                    end
                  end

            
                  col&.map! do |cel|
                    next if cel.nil?

                    if cel['host'] == 'TBD' || cel['host'] == 'null'
                      cel.delete('host')
                    elsif cel['host'] != nil
                      cel['host'] = cluster_map['hostPrefix'] + cel['host']
                    end

                    cel
                  end
                end

                cluster_map
              }
              .sort_by { |cluster_map| cluster_map['position'] }
              .group_by { |cluster_map| cluster_map['campusId'] }

maps.each do |campus_id, cluster_maps|
  File.write(File.join(OUTPUT_PATH, "cluster_map_campus_#{campus_id}.json"), cluster_maps.to_json)
end
