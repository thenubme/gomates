import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useLocation } from "wouter";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useToast } from "@/hooks/use-toast";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { insertRideSchema } from "@shared/schema";
import { getStaticMapUrl } from "@/lib/maps";
import { Card, CardContent } from "@/components/ui/card";
import { useState } from "react";
import { apiRequest } from "@/lib/queryClient";

const postRideSchema = insertRideSchema.extend({
  departureTime: z.string(),
});

type PostRideForm = z.infer<typeof postRideSchema>;

export default function PostRidePage() {
  const [, setLocation] = useLocation();
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const user = JSON.parse(localStorage.getItem("user") || "null");
  const [mapUrl, setMapUrl] = useState<string>("");

  const form = useForm<PostRideForm>({
    resolver: zodResolver(postRideSchema),
    defaultValues: {
      origin: "",
      destination: "",
      originLat: "0",
      originLng: "0",
      destinationLat: "0",
      destinationLng: "0",
      fare: 0,
      seats: 1,
      departureTime: "",
    },
  });

  if (!user) {
    setLocation("/");
    return null;
  }

  const { mutate, isPending } = useMutation({
    mutationFn: async (data: PostRideForm) => {
      const payload = {
        ...data,
        userId: user.id,
        fare: parseInt(data.fare.toString()),
        seats: parseInt(data.seats.toString()),
        departureTime: new Date(data.departureTime).toISOString(),
      };
      const response = await apiRequest("POST", "/api/rides", payload);
      const result = await response.json();
      return result;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/rides"] });
      setLocation("/dashboard");
      toast({
        title: "Success",
        description: "Ride posted successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        variant: "destructive",
        title: "Error",
        description: error.message,
      });
    },
  });

  const updateMap = async (origin: string, destination: string) => {
    try {
      // For demo, using fixed coordinates
      const originCoords = { lat: 12.9716, lng: 77.5946 };
      const destCoords = { lat: 13.0827, lng: 77.5877 };

      form.setValue("originLat", originCoords.lat.toString());
      form.setValue("originLng", originCoords.lng.toString());
      form.setValue("destinationLat", destCoords.lat.toString());
      form.setValue("destinationLng", destCoords.lng.toString());

      const url = getStaticMapUrl(originCoords, destCoords);
      setMapUrl(url);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load map",
      });
    }
  };

  return (
    <div className="container max-w-2xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">Post a New Ride</h1>

      <Form {...form}>
        <form onSubmit={form.handleSubmit((data) => mutate(data))} className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <FormField
              control={form.control}
              name="origin"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Origin</FormLabel>
                  <FormControl>
                    <Input 
                      placeholder="Enter pickup location"
                      {...field}
                      onChange={(e) => {
                        field.onChange(e);
                        updateMap(e.target.value, form.getValues("destination"));
                      }}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="destination"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Destination</FormLabel>
                  <FormControl>
                    <Input 
                      placeholder="Enter drop-off location"
                      {...field}
                      onChange={(e) => {
                        field.onChange(e);
                        updateMap(form.getValues("origin"), e.target.value);
                      }}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          {mapUrl && (
            <Card className="mb-4">
              <CardContent className="p-2">
                <img
                  src={mapUrl}
                  alt="Route map"
                  className="w-full h-[300px] object-cover rounded"
                />
              </CardContent>
            </Card>
          )}

          <div className="grid gap-4 md:grid-cols-3">
            <FormField
              control={form.control}
              name="fare"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Fare ($)</FormLabel>
                  <FormControl>
                    <Input type="number" min="0" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="seats"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Available Seats</FormLabel>
                  <FormControl>
                    <Input type="number" min="1" max="8" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="departureTime"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Departure Time</FormLabel>
                  <FormControl>
                    <Input type="datetime-local" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <div className="flex justify-end space-x-4">
            <Button
              type="button"
              variant="outline"
              onClick={() => setLocation("/dashboard")}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isPending}>
              {isPending ? "Posting..." : "Post Ride"}
            </Button>
          </div>
        </form>
      </Form>
    </div>
  );
}