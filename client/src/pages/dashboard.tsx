import { useQuery } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import RideCard from "@/components/ride-card";
import { Plus, ListPlus } from "lucide-react";
import { useLocation } from "wouter";
import type { Ride } from "@shared/schema";

export default function DashboardPage() {
  const [, setLocation] = useLocation();
  const user = JSON.parse(localStorage.getItem("user") || "null");

  const { data: rides, isLoading } = useQuery<Ride[]>({
    queryKey: ["/api/rides"],
  });

  if (!user) {
    setLocation("/");
    return null;
  }

  const userRides = rides?.filter(ride => ride.userId === user.id) || [];

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-4">
            <Button 
              onClick={() => setLocation("/post-ride")}
              className="w-full"
            >
              <Plus className="mr-2 h-4 w-4" />
              Post a New Ride
            </Button>
            <Button 
              variant="outline" 
              onClick={() => setLocation("/rides")}
              className="w-full"
            >
              <ListPlus className="mr-2 h-4 w-4" />
              Find Available Rides
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Your Posted Rides</CardTitle>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="text-center text-muted-foreground">Loading...</div>
            ) : userRides.length === 0 ? (
              <div className="text-center text-muted-foreground">
                You haven't posted any rides yet
              </div>
            ) : (
              <div className="space-y-4">
                {userRides.map((ride) => (
                  <RideCard key={ride.id} ride={ride} />
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
