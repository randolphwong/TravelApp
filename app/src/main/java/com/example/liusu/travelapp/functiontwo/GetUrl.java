package com.example.liusu.travelapp.functiontwo;

/**
 * Created by liusu on 15/11/15.
 */
public class GetUrl {
    public static String getUrl(String attraction){
        switch (attraction){
            case "marina bay sands":
                return "https://www.tripadvisor.com.sg/Tourism-g2146381-Marina_Bay-Vacations.html";
            case "singapore flyer":
                return "https://www.tripadvisor.com.sg/Attraction_Review-g294265-d678639-Reviews-Singapore_Flyer-Singapore.html";
            case "vivo city":
                return "https://www.tripadvisor.com.sg/Attraction_Review-g294265-d634131-Reviews-Vivo_City-Singapore.html";
            case "resort world sentosa":
                return "https://www.tripadvisor.com.sg/Tourism-g294264-Sentosa_Island-Vacations.html";
            case "buddha tooth relic temple":
                return "https://www.tripadvisor.com.my/Attraction_Review-g294265-d1438273-Reviews-Buddha_Tooth_Relic_Temple_and_Museum-Singapore.html";
            case "singapore zoo":
                return "https://www.tripadvisor.com.my/Attraction_Review-g294265-d324542-Reviews-Singapore_Zoo-Singapore.html";
            case "gardens by the bay":
                return "https://www.tripadvisor.com.my/Attraction_Review-g294265-d2149128-Reviews-Gardens_By_The_Bay-Singapore.html";
            case "gmax reverse bungy":
                return "https://www.tripadvisor.com.my/Attraction_Review-g294265-d941492-Reviews-G_MAX_Reverse_Bungy-Singapore.html";
            case "fort canning park":
                return "https://www.tripadvisor.com.my/Attraction_Review-g294265-d324755-Reviews-Fort_Canning_Park-Singapore.html";
        }
        return null;
    }
}
