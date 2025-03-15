package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.whatsapp.NextMessageMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NextMessageMappingRepository extends JpaRepository<NextMessageMapping, Long> {}
