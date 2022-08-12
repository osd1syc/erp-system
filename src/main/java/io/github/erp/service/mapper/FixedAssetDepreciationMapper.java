package io.github.erp.service.mapper;

/*-
 * Erp System - Mark II No 26 (Baruch Series) Server ver 0.0.6-SNAPSHOT
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
import io.github.erp.domain.FixedAssetDepreciation;
import io.github.erp.service.dto.FixedAssetDepreciationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FixedAssetDepreciation} and its DTO {@link FixedAssetDepreciationDTO}.
 */
@Mapper(componentModel = "spring", uses = { PlaceholderMapper.class })
public interface FixedAssetDepreciationMapper extends EntityMapper<FixedAssetDepreciationDTO, FixedAssetDepreciation> {
    @Mapping(target = "placeholders", source = "placeholders", qualifiedByName = "descriptionSet")
    FixedAssetDepreciationDTO toDto(FixedAssetDepreciation s);

    @Mapping(target = "removePlaceholder", ignore = true)
    FixedAssetDepreciation toEntity(FixedAssetDepreciationDTO fixedAssetDepreciationDTO);
}
