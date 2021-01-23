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
}
