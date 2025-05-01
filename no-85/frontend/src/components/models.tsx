export interface TimeZoneResults {
  source: TimeZoneResult;
  targets: TimeZoneResult[];
  all: TimeZoneResult[];
}

export interface TimeZoneResult {
  id: string; // "Asia/Tokyo;
  offset: string; // "+09:00";
  offsetIndex: number; // 0;
  hours: Hours[];
}

export interface Hours {
  hour: number; // 17;
  utcHour: number; // 9;
  time: string; // "2025-03-26T17:44:44.688522Z";
  offset_to_utc: string; // "+09:00";
  offset_to_source?: string; // "+09:00";
}
