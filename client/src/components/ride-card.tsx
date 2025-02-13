import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { type Ride } from "@shared/schema";
import { format } from "date-fns";
import { MapPin } from "lucide-react";

interface RideCardProps {
  ride: Ride;
}

export default function RideCard({ ride }: RideCardProps) {
  return (
    <Card>
      <CardHeader className="pb-3">
        <CardTitle className="text-lg font-medium flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Avatar className="h-6 w-6">
              <AvatarFallback>
                {ride.origin.charAt(0).toUpperCase()}
              </AvatarFallback>
            </Avatar>
            <span>{ride.origin}</span>
          </div>
          <span className="text-sm font-normal text-muted-foreground">
            ${ride.fare}
          </span>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="flex items-center space-x-2 text-sm">
            <MapPin className="h-4 w-4 text-muted-foreground" />
            <span>{ride.destination}</span>
          </div>
          <div className="flex items-center justify-between text-sm text-muted-foreground">
            <span>{format(new Date(ride.departureTime), "PPp")}</span>
            <span>{ride.seats} seats available</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
