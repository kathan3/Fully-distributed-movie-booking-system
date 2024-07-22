package com.microservice.h2db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.microservice.h2db.Models.Show;
import com.microservice.h2db.Models.Theatre;
import com.microservice.h2db.Repository.ShowDBRepository;
import com.microservice.h2db.Repository.TheaterDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class MyCommandLineRunner implements CommandLineRunner {
    @Autowired
    TheaterDBRepository theatreRepo;
    @Autowired
    ShowDBRepository showRepo;



    public void TheatreRead(){
        String line="";
        try{
            BufferedReader br=new BufferedReader(new FileReader("src/main/resources/theatres.csv.txt"));
            while((line=br.readLine())!=null){
                String[] shows=line.split(",");
                Theatre theatre=new Theatre(Integer.parseInt(shows[0]),shows[1],shows[2]);
                theatreRepo.save(theatre);
            }
            br.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void ShowRead(){
        String line="";
        try{
            BufferedReader br=new BufferedReader(new FileReader("src/main/resources/shows.csv.txt"));
            while((line=br.readLine())!=null){
                String[] shows=line.split(",");
                Show show=new Show(Integer.parseInt(shows[0]),Integer.parseInt(shows[1]),shows[2],Integer.parseInt(shows[3]),Integer.parseInt(shows[4]));
                showRepo.save(show);
            }
            br.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception{
        TheatreRead();
        ShowRead();
    }
}
