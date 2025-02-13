interface Coordinates {
  lat: number;
  lng: number;
}

export function getStaticMapUrl(origin: Coordinates, destination: Coordinates): string {
  // Replace YOUR_API_KEY with actual key in production
  const API_KEY = "YOUR_API_KEY_HERE";
  
  // Center the map between origin and destination
  const centerLat = (origin.lat + destination.lat) / 2;
  const centerLng = (origin.lng + destination.lng) / 2;
  
  // Build the URL
  const baseUrl = "https://maps.googleapis.com/maps/api/staticmap";
  const params = new URLSearchParams({
    center: `${centerLat},${centerLng}`,
    zoom: "13",
    size: "600x300",
    key: API_KEY,
    markers: [
      `color:red|label:A|${origin.lat},${origin.lng}`,
      `color:blue|label:B|${destination.lat},${destination.lng}`
    ].join("&markers=")
  });

  return `${baseUrl}?${params.toString()}`;
}
