import React, { useState, useEffect } from "react";
import { timezoneConverter, TimezoneConverterRequest } from "./api-hooks";
import { Hours, TimeZoneResults } from "./models";

const timeZoneConverterRequest = {
  source: "Europe/London",
  cities: ["Toronto", "Zurich", "Dubai", "Tokyo", "Sydney"],
} as TimezoneConverterRequest;

const formatHour = (hour: number) => {
  const h = hour % 12 || 12;
  const suffix = hour < 12 ? "am" : "pm";
  return `${h} ${suffix}`;
};

const TimeZoneComparison: React.FC = () => {
  const [currentUtc, setCurrentUtc] = useState(() => {
    // get the current UTC time when the component mounts
    const currentDate = new Date();
    const utcDate = new Date(
      currentDate.getUTCFullYear(),
      currentDate.getUTCMonth(),
      currentDate.getUTCDate(),
      currentDate.getUTCHours(),
      currentDate.getUTCMinutes(),
      currentDate.getUTCSeconds()
    );
    const utcString = utcDate.toISOString();
    return utcString;
  });

  const [currentUtcHour, setCurrentUtcHour] = useState(() => {
    // Get the current UTC hour when the component mounts
    const currentDate = new Date().getUTCHours();
    console.log(currentDate);
    return currentDate;
  });
  const [timezoneResults, setTimezoneResults] =
    useState<TimeZoneResults | null>(null);

  useEffect(() => {
    const fetchTimezoneResults = async () => {
      const result = await timezoneConverter(
        currentUtc,
        timeZoneConverterRequest
      );
      console.log(result);
      setTimezoneResults(result);
    };

    fetchTimezoneResults();
  }, [currentUtc]);

  useEffect(() => {
    const interval = setInterval(() => {
      const currentDate = new Date().getUTCHours();
      console.log(currentDate);
      setCurrentUtcHour(currentDate);
    }, 5000); // Update every minute
    return () => clearInterval(interval);
  }, []);

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
                  const localDay = "Same day";
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
                        {"Mon"}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          ))}
      </div>
    </div>
  );
};

export default TimeZoneComparison;
