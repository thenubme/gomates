import { useQuery } from "@tanstack/react-query";
import { Input } from "@/components/ui/input";
import RideCard from "@/components/ride-card";
import type { Ride } from "@shared/schema";
import { useState } from "react";
import { Search } from "lucide-react";

export default function RideListPage() {
  const [search, setSearch] = useState("");
  
  const { data: rides, isLoading } = useQuery<Ride[]>({
    queryKey: ["/api/rides"],
  });

  const filteredRides = rides?.filter(ride => 
    ride.origin.toLowerCase().includes(search.toLowerCase()) ||
    ride.destination.toLowerCase().includes(search.toLowerCase())
  ) || [];

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-3xl font-bold mb-6">Available Rides</h1>

        <div className="relative mb-6">
          <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
          <Input
            className="pl-10"
            placeholder="Search by origin or destination"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        {isLoading ? (
          <div className="text-center text-muted-foreground">Loading rides...</div>
        ) : filteredRides.length === 0 ? (
          <div className="text-center text-muted-foreground">
            {search ? "No rides found matching your search" : "No rides available"}
          </div>
        ) : (
          <div className="space-y-4">
            {filteredRides.map((ride) => (
              <RideCard key={ride.id} ride={ride} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
