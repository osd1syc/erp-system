package io.github.erp.internal.model;

/*-
 * Erp System - Mark X No 10 (Jehoiada Series) Server ver 1.8.2
 * Copyright © 2021 - 2024 Edwin Njeru and the ERP System Contributors (mailnjeru@gmail.com)
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
import io.github.erp.domain.enumeration.DepreciationRegime;
import io.github.erp.internal.framework.batch.BatchEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Batch entity object for the fixed-asset-NBV entity
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedAssetNetBookValueBEO implements BatchEntity {

    private Long id;

    private Long assetNumber;

    private String serviceOutletCode;

    private String assetTag;

    private String assetDescription;

    private LocalDate netBookValueDate;

    private String assetCategory;

    private BigDecimal netBookValue;

    private DepreciationRegime depreciationRegime;

    private String fileUploadToken;

    private String compilationToken;

    @Override
    public void setUploadToken(String uploadToken) {
        this.fileUploadToken = uploadToken;
    }

    @Override
    public String getUploadToken() {
        return this.fileUploadToken;
    }
}
