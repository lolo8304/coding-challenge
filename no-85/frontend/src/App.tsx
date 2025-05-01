import "./App.css";
import TimeZoneComparison from "./components/TimeZoneComparison";
import TimezoneDropdown from "./components/timezones";

function App() {
  return (
    <div className="App">
      <TimezoneDropdown />
      <TimeZoneComparison />
    </div>
  );
}

export default App;
