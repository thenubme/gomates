import { useLocation } from "wouter";
import { Button } from "@/components/ui/button";
import { Car } from "lucide-react";

export default function Navbar() {
  const [location, setLocation] = useLocation();
  const user = JSON.parse(localStorage.getItem("user") || "null");

  const handleLogout = () => {
    localStorage.removeItem("user");
    setLocation("/");
  };

  if (!user) return null;

  return (
    <nav className="border-b">
      <div className="container mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Car className="h-6 w-6" />
          <span className="font-bold text-xl">GoMates</span>
        </div>

        <div className="flex items-center space-x-4">
          <Button
            variant={location === "/dashboard" ? "default" : "ghost"}
            onClick={() => setLocation("/dashboard")}
          >
            Dashboard
          </Button>
          <Button
            variant={location === "/rides" ? "default" : "ghost"}
            onClick={() => setLocation("/rides")}
          >
            Find Rides
          </Button>
          <Button
            variant={location === "/post-ride" ? "default" : "ghost"}
            onClick={() => setLocation("/post-ride")}
          >
            Post Ride
          </Button>
          <Button variant="outline" onClick={handleLogout}>
            Logout
          </Button>
        </div>
      </div>
    </nav>
  );
}
