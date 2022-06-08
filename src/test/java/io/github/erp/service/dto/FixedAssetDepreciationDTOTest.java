package io.github.erp.service.dto;

/*-
 * Erp System - Mark II No 7 (Artaxerxes Series)
 * Copyright © 2021 Edwin Njeru (mailnjeru@gmail.com)
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

import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.utils.TestUtil;
import org.junit.jupiter.api.Test;

class FixedAssetDepreciationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FixedAssetDepreciationDTO.class);
        FixedAssetDepreciationDTO fixedAssetDepreciationDTO1 = new FixedAssetDepreciationDTO();
        fixedAssetDepreciationDTO1.setId(1L);
        FixedAssetDepreciationDTO fixedAssetDepreciationDTO2 = new FixedAssetDepreciationDTO();
        assertThat(fixedAssetDepreciationDTO1).isNotEqualTo(fixedAssetDepreciationDTO2);
        fixedAssetDepreciationDTO2.setId(fixedAssetDepreciationDTO1.getId());
        assertThat(fixedAssetDepreciationDTO1).isEqualTo(fixedAssetDepreciationDTO2);
        fixedAssetDepreciationDTO2.setId(2L);
        assertThat(fixedAssetDepreciationDTO1).isNotEqualTo(fixedAssetDepreciationDTO2);
        fixedAssetDepreciationDTO1.setId(null);
        assertThat(fixedAssetDepreciationDTO1).isNotEqualTo(fixedAssetDepreciationDTO2);
    }
}
