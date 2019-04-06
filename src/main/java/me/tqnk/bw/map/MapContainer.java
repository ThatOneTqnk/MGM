package me.tqnk.bw.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@AllArgsConstructor @Getter
public class MapContainer {
    private File rawFolder;
    private MapInfo metadata;
}
