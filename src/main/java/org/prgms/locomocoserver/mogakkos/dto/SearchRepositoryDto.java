package org.prgms.locomocoserver.mogakkos.dto;

import org.prgms.locomocoserver.location.domain.LocationRepository;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;

public record SearchRepositoryDto (MogakkoRepository mogakkoRepository,
                                   LocationRepository locationRepository,
                                   MogakkoTagRepository mogakkoTagRepository
                                   ) {
}
