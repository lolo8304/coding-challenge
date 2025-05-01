import React, { useEffect, useState } from "react";
import { ComposableMap, Geographies, Geography, Marker } from "react-simple-maps";

const geoUrl =
  "https://raw.githubusercontent.com/deldersveld/topojson/master/world-countries.json";

interface WorldMapProps {
  timezones: string[];
}

interface MarkerData {
  name: string;
  coordinates: [number, number]; // [lng, lat]
}

const WorldMapWithTimezones: React.FC<WorldMapProps> = ({ timezones }) => {
  const [markers, setMarkers] = useState<MarkerData[]>([]);

  useEffect(() => {
    const fetchCoordinates = async () => {
      const fetchedMarkers: MarkerData[] = [];

      await Promise.all(
        timezones.map(async (tz) => {
          try {
            const res = await fetch(
              `https://secure.geonames.org/timezoneJSON?username=demo&tz=${encodeURIComponent(
                tz
              )}`
            );
            const data = await res.json();

            if (data.lat && data.lng) {
              fetchedMarkers.push({
                name: tz,
                coordinates: [data.lng, data.lat],
              });
            }
          } catch (err) {
            console.error(`Failed to fetch coordinates for ${tz}`, err);
          }
        })
      );

      setMarkers(fetchedMarkers);
    };

    fetchCoordinates();
  }, [timezones]);

  return (
    <div className="w-full h-[500px]">
      <ComposableMap projectionConfig={{ scale: 170 }}>
        <Geographies geography={geoUrl}>
          {({ geographies }) =>
            geographies.map((geo) => (
              <Geography key={geo.rsmKey} geography={geo} fill="#EAEAEC" stroke="#D6D6DA" />
            ))
          }
        </Geographies>
        {markers.map((marker) => (
          <Marker key={marker.name} coordinates={marker.coordinates}>
            <circle r={5} fill="#F00" stroke="#fff" strokeWidth={1} />
            <text textAnchor="middle" y={-10} style={{ fontSize: 10 }}>
              {marker.name}
            </text>
          </Marker>
        ))}
      </ComposableMap>
    </div>
  );
};

export default WorldMapWithTimezones;
