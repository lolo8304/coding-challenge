import React, { useState, useEffect } from "react";
import { timezoneConverter, TimezoneConverterRequest } from "./api-hooks";
import { Hours, TimeZoneResults } from "./models";
import WorldMapWithTimezones from "./WorldMapWithTimezones";

const timeZoneConverterRequest = {
  source: "Europe/Zurich",
  cities: [
    "GMT+12",
    "Honolulu",
    "Toronto",
    "London",
    "Madrid",
    "Los_Angeles",
    "Paris",
    "Berlin",
    "Rome",
    "Mexico_City",
    "Dubai",
    "Tokyo",
    "New_York",
    "Sydney",
    "GMT-14",
  ],
} as TimezoneConverterRequest;

const formatHour = (hour: number) => {
  const h = hour % 12 || 12;
  const suffix = hour < 12 ? "am" : "pm";
  return `${h} ${suffix}`;
};
const currentUtcHours = () => {
  return new Date().getUTCHours();
};
const currentUtcString = () => {
  const currentDate = new Date();
  const utcDate = new Date(
    currentDate.getUTCFullYear(),
    currentDate.getUTCMonth(),
    currentDate.getUTCDate(),
    currentDate.getUTCHours(),
    currentDate.getUTCMinutes(),
    currentDate.getUTCSeconds()
  );
  return utcDate.toISOString();
};

const TimeZoneComparison: React.FC = () => {
  const [currentUtc, setCurrentUtc] = useState(() => {
    return currentUtcString();
  });

  const [currentUtcHour, setCurrentUtcHour] = useState(() => {
    return currentUtcHours();
  });
  const [timezoneResults, setTimezoneResults] =
    useState<TimeZoneResults | null>(null);

  useEffect(() => {
    const fetchTimezoneResults = async () => {
      const result = await timezoneConverter(
        currentUtc,
        timeZoneConverterRequest
      );
      setTimezoneResults(result);
    };

    fetchTimezoneResults();
  }, [currentUtc]);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentUtc(currentUtcString());
      setCurrentUtcHour(currentUtcHours());
    }, 60000); // Update every minute
    return () => clearInterval(interval);
  }, []);
/*
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentUtcHour((prev) => (prev + 1) % 24);
    }, 2000); // Update every minute
    return () => clearInterval(interval);
  }, []);
*/
  return (
    <div
      style={{
        overflowX: "auto",
        padding: "10px",
        fontFamily: "Arial, sans-serif",
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "20px",
          minWidth: "1200px",
        }}
      >
        <div
          style={{
            textAlign: "left",
            fontWeight: "bold",
            marginBottom: "5px",
          }}
        >
          Current UTC: {currentUtc}
        </div>
        {!timezoneResults && <p>Loading...</p>}
        {timezoneResults &&
          timezoneResults.all.map((tz) => (
            <div key={tz.id}>
              <div
                style={{
                  textAlign: "left",
                  fontWeight: "bold",
                  marginBottom: "5px",
                }}
              >
                {tz.offset} {tz.id}
              </div>
              <div style={{ display: "flex" }}>
                {tz.hours.map((hour: Hours, hIndex) => {
                  const sourceHour =
                    timezoneResults.source.hours[hIndex].utcHour;
                  const isCurrentHour = hour.utcHour === currentUtcHour;

                  return (
                    <div
                      key={sourceHour}
                      style={{
                        minWidth: "50px",
                        height: "40px",
                        textAlign: "center",
                        border: "1px solid #ddd",
                        backgroundColor: isCurrentHour ? "#c8f7c5" : "#fff",
                        fontSize: "12px",
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "center",
                      }}
                    >
                      <div>{formatHour(hour.hour)}</div>
                      <div style={{ fontSize: "10px", color: "#999" }}>
                        {"Day X"}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          ))}
        <div className="w-400px">
          <WorldMapWithTimezones
            timezones={["Europe/Zurich", "America/Toronto"]}
          />
        </div>
      </div>
    </div>
  );
};

export default TimeZoneComparison;
