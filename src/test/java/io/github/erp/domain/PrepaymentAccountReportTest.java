package io.github.erp.domain;

/*-
 * Erp System - Mark X No 7 (Jehoiada Series) Server ver 1.7.8
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
import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PrepaymentAccountReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepaymentAccountReport.class);
        PrepaymentAccountReport prepaymentAccountReport1 = new PrepaymentAccountReport();
        prepaymentAccountReport1.setId(1L);
        PrepaymentAccountReport prepaymentAccountReport2 = new PrepaymentAccountReport();
        prepaymentAccountReport2.setId(prepaymentAccountReport1.getId());
        assertThat(prepaymentAccountReport1).isEqualTo(prepaymentAccountReport2);
        prepaymentAccountReport2.setId(2L);
        assertThat(prepaymentAccountReport1).isNotEqualTo(prepaymentAccountReport2);
        prepaymentAccountReport1.setId(null);
        assertThat(prepaymentAccountReport1).isNotEqualTo(prepaymentAccountReport2);
    }
}
