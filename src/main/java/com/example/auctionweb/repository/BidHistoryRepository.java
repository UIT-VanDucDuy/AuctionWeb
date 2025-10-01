package com.example.auctionweb.repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import com.example.auctionweb.entity.BidHistory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class BidHistoryRepository implements IBidHistoryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BidHistory> findAll() {
        // kế nối
        List<BidHistory> studentList = new ArrayList<>();
        TypedQuery<BidHistory> query = entityManager.createQuery("from BidHistory",BidHistory.class);
//        TypedQuery<Student> query = session.createNativeQuery("select * from student",Student.class);
        studentList = query.getResultList();
        return studentList;
    }
    @Transactional
    @Override
    public boolean add(BidHistory bidHistory) {
        try{
            entityManager.persist(bidHistory);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
