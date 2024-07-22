package com.podsProject.showService.showService.repository;

import com.podsProject.showService.showService.ShowServiceApplication;
import com.podsProject.showService.showService.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface ShowReoisitory extends JpaRepository<Show,Integer> {
    public List<Show> findByTheatreId(Integer TheatreId);
}
