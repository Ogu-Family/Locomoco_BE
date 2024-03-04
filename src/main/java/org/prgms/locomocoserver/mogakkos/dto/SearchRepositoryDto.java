package org.prgms.locomocoserver.mogakkos.dto;

import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;

public record SearchRepositoryDto (MogakkoRepository mogakkoRepository,
                                   MogakkoTagRepository mogakkoTagRepository
                                   ) {
}
