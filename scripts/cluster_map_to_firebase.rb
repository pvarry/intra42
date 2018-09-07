#!/usr/bin/ruby

require 'json'

SOURCE_PATH = File.join(__dir__, '..', 'app', 'src', 'main', 'res', 'raw')
MAP_FILES = Dir.glob(File.join(SOURCE_PATH, 'cluster_map_campus_*.json'))

campus_cluster_maps = MAP_FILES.reduce({}) do |acc, filename|
  campus_cluster_map = JSON.parse(File.read(filename))
  campus_cluster_map.each do |old_cluster|
    new_cluster = {
      campusId: old_cluster['campus_id'], comment:    old_cluster['comment'],
      name:     old_cluster['name'],      nameShort:  old_cluster['nameShort'],
      position: old_cluster['position'],  hostPrefix: old_cluster['host_prefix'],
      width:    old_cluster['width'],     height:     old_cluster['height'],
      map:      old_cluster['map'],       isReadyToPublish: old_cluster['isReadyToPublish'],
      slug:     old_cluster['slug'] || "#{old_cluster['campus_id']}_#{old_cluster['host_prefix']}",
    }

    new_cluster[:map]&.map! do |column|
      next unless column

      column.map.with_index do |old_cell, index|
        next [index, nil] unless old_cell

        [index, {
          host:  old_cell['host']&.sub(new_cluster[:hostPrefix], ''),
          sizeX: old_cell['scale_x'], sizeY: old_cell['scale_y'],
          angle: old_cell['rot'],     kind:  old_cell['kind']&.upcase
        }]
      end.to_h
    end

    File.write("[#{new_cluster[:campusId]}] #{new_cluster[:name]}.json", new_cluster.to_json)

    acc[new_cluster[:slug]] = new_cluster
  end

  acc
end

File.write('all.json', campus_cluster_maps.to_json)