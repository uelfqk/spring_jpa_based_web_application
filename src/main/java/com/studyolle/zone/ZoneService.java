package com.studyolle.zone;

import com.studyolle.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ZoneService {
    private final ZoneRepository zoneRepository;

    //TODO 2021.01.24 42.지역 정보 도메인
    //     1. 위키피디아의 대한민국 지역정보를 zones_kr.csv 파일로 저장하고
    //        Resource 로 데이터를 읽어와 데이터베이스 Zone 테이블에 저장
    //     2. Seoul,서울특별시,none 형태의 정보를 ',' 으로 분리하여
    //        각 city, localNameOfCity, province 로 저장
    //     3. Spring Ioc Container 가 제공하는 생명주기 인터페이스인 @PostConstruct 를 이용해
    //        ZoneService 스프링 빈이 생성될때 저장하도록 구현
    @PostConstruct
    public void initZoneData() throws IOException {
        if(zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> result = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                    .stream().map(line -> {
                        String[] split = line.split(",");
                        return Zone.createZone(split[0], split[1], split[2]);

                    }).collect(Collectors.toList());
            zoneRepository.saveAll(result);
        }
    }

    public List<String> getZoneWhiteList() {
        return zoneRepository.findAll().stream()
                .map(z -> z.toString())
                .collect(Collectors.toList());
    }
}
