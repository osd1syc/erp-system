package io.github.erp.service.dto;

/*-
 * Erp System - Mark II No 28 (Baruch Series) Server ver 0.0.9-SNAPSHOT
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
import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CountyCodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CountyCodeDTO.class);
        CountyCodeDTO countyCodeDTO1 = new CountyCodeDTO();
        countyCodeDTO1.setId(1L);
        CountyCodeDTO countyCodeDTO2 = new CountyCodeDTO();
        assertThat(countyCodeDTO1).isNotEqualTo(countyCodeDTO2);
        countyCodeDTO2.setId(countyCodeDTO1.getId());
        assertThat(countyCodeDTO1).isEqualTo(countyCodeDTO2);
        countyCodeDTO2.setId(2L);
        assertThat(countyCodeDTO1).isNotEqualTo(countyCodeDTO2);
        countyCodeDTO1.setId(null);
        assertThat(countyCodeDTO1).isNotEqualTo(countyCodeDTO2);
    }
}
