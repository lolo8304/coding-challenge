import React, { useMemo, useState } from "react";
import { useTimezones } from "./api-hooks";

const TimezoneDropdown: React.FC = () => {
  const { timezones, isLoading, isError } = useTimezones();

  const continents = useMemo(() => {
    return timezones?.reduce((acc: string[], timezone: string) => {
      const continent = timezone.split("/")[0];
      if (!acc.includes(continent)) {
        acc.push(continent);
      }
      return acc;
    }, []);
  }, [timezones]);
  const [selectedContinent, setSelectedContinent] = useState<string | null>(
    // get current timezone continent
    () => {
      const currentTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
      return currentTimezone.split("/")[0];
    }
  );
  const handleContinentChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setSelectedContinent(event.target.value);
  };
  const filteredTimezones = timezones
    ?.filter((timezone) => {
      if (!selectedContinent) return true;
      return timezone.startsWith(selectedContinent);
    })
    .map((timezone) => {
      const parts = timezone.split("/");
      return parts.length > 1 ? parts[1] : timezone;
    });
  const uniqueTimezones = useMemo(() => {
    return filteredTimezones
      ?.reduce((acc: string[], timezone: string) => {
        if (!acc.includes(timezone)) {
          acc.push(timezone);
        }
        return acc;
      }, [])
      .sort();
  }, [filteredTimezones]);
  const [selectedTimezone, setSelectedTimezone] = useState<string | null>(
    () => {
      const currentTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
      return currentTimezone.split("/")[1];
    }
  );
  const handleTimezoneChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setSelectedTimezone(event.target.value);
  };

  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedTimezone(event.target.value);
  };

  return (
    <div>
      {isLoading && <p>Loading timezones...</p>}
      {isError && <p>Error loading timezones</p>}
      {!isLoading && !isError && timezones && (
        <>
          <select id="continent-select" onChange={handleContinentChange}>
            <option value="">Select a continent</option>
            {continents?.map((continent) => (
              <option key={continent} value={continent}>
                {continent}
              </option>
            ))}
          </select>
          <select id="timezone-select" onChange={handleTimezoneChange}>
            <option value="">Select a timezone</option>
            {uniqueTimezones?.map((timezone) => (
              <option key={timezone} value={timezone}>
                {timezone}
              </option>
            ))}
          </select>
        </>
      )}
    </div>
  );
};

export default TimezoneDropdown;
