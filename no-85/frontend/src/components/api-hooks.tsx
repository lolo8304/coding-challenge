import useSWR from "swr";
import { Hours, TimeZoneResult, TimeZoneResults } from "./models";

const BASE_URL = process.env.REACT_APP_BASE_URL;

const fetcher = (url: string) => fetch(url).then((res) => res.json());

export const useTimezones = () => {
  const { data, error } = useSWR<string[]>(`${BASE_URL}/timezones`, fetcher);

  return {
    timezones: data,
    isLoading: !error && !data,
    isError: error,
  };
};

export interface TimezoneConverterRequest {
  source: string;
  timezones?: string[];
  cities?: string[];
  countries?: string[];
}

function getTz(tzMap: Map<string, TimeZoneResult>, id: string) {
  if (!tzMap.has(id)) {
    tzMap.set(id, {
      id,
      hours: [],
      offsetIndex: 0,
      offset: "+00:00",
    } as TimeZoneResult);
  }
  return tzMap.get(id)!;
}

function offset(hhmm: string) {
  // convert +hh:mm to hh as integer and -hh:mm to -hh as integer
  const sign = hhmm[0] === "+" ? 1 : -1;
  const [hh, mm] = hhmm.slice(1).split(":").map(Number);
  return sign * (hh + mm / 60);
}

export async function timezoneConverter(
  utc: string | undefined,
  request: TimezoneConverterRequest
): Promise<TimeZoneResults> {
  const utcString = utc ? "?utc=" + utc : "";
  const queryString = (utcString ? utcString : "?") + "&hours=24";
  const response = await fetch(
    `${process.env.REACT_APP_BASE_URL}/timezone-converter${queryString}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    throw new Error("Failed to update task trial");
  }
  const results = (await response.json()) as unknown as any[];
  const timezoneHours: TimeZoneResult[] = [];
  if (results.length === 0)
    return {
      source: {} as TimeZoneResult,
      targets: [],
      all: [],
    } as TimeZoneResults;
  const tzMap = new Map<string, TimeZoneResult>();
  results.forEach((result, i) => {
    const tzS = getTz(tzMap, result.source_id);
    tzS.offset = result.source_offset;
    const offsetI = offset(result.source_offset);
    tzS.offsetIndex = offsetI;
    const time = result.source_time;
    const h = parseInt(time.split("T")[1].split(":")[0]);

    const hour = {
      hour: h,
      utcHour: (h - offsetI + 24) % 24,
      time,
      offset_to_utc: result.source_offset,
      offset_to_source: "+00:00",
    } as Hours;
    tzS.hours.push(hour);
    if (i === 0) {
      timezoneHours.push(tzS);
    }

    let index = 0;
    let id = result[`target_${index}_id`];
    while (id) {
      const tz = getTz(tzMap, id);
      tz.offset = result[`target_${index}_offset_to_utc`];
      const offsetI = offset(result[`target_${index}_offset_to_utc`]);
      tz.offsetIndex = tzS.offsetIndex + offsetI;
      const time = result[`target_${index}_time`];
      const h = parseInt(time.split("T")[1].split(":")[0]);
      const hour = {
        hour: h,
        utcHour: (h - offsetI + 24) % 24,
        time,
        offset_to_utc: result[`target_${index}_offset_to_utc`],
        offset_to_source: result[`target_${index}_offset_to_source`],
      } as Hours;
      tz.hours.push(hour);
      if (i === 0) {
        timezoneHours.push(tz);
      }
      index++;
      id = result[`target_${index}_id`];
    }
  });
  timezoneHours.forEach((tz) => {
    tz.hours.sort((a, b) => a.hour - b.hour);
  });
  return {
    source: timezoneHours[0],
    targets: timezoneHours.slice(1),
    all: timezoneHours,
  } as TimeZoneResults;
}
