package io.github.erp.service.mapper;

/*-
 * Erp System - Mark III No 2 (Caleb Series) Server ver 0.1.2-SNAPSHOT
 * Copyright © 2021 - 2022 Edwin Njeru (mailnjeru@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import io.github.erp.domain.SecurityClearance;
import io.github.erp.service.dto.SecurityClearanceDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SecurityClearance} and its DTO {@link SecurityClearanceDTO}.
 */
@Mapper(componentModel = "spring", uses = { PlaceholderMapper.class })
public interface SecurityClearanceMapper extends EntityMapper<SecurityClearanceDTO, SecurityClearance> {
    @Mapping(target = "grantedClearances", source = "grantedClearances", qualifiedByName = "clearanceLevelSet")
    @Mapping(target = "placeholders", source = "placeholders", qualifiedByName = "descriptionSet")
    SecurityClearanceDTO toDto(SecurityClearance s);

    @Mapping(target = "removeGrantedClearances", ignore = true)
    @Mapping(target = "removePlaceholder", ignore = true)
    SecurityClearance toEntity(SecurityClearanceDTO securityClearanceDTO);

    @Named("clearanceLevel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "clearanceLevel", source = "clearanceLevel")
    SecurityClearanceDTO toDtoClearanceLevel(SecurityClearance securityClearance);

    @Named("clearanceLevelSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "clearanceLevel", source = "clearanceLevel")
    Set<SecurityClearanceDTO> toDtoClearanceLevelSet(Set<SecurityClearance> securityClearance);
}
