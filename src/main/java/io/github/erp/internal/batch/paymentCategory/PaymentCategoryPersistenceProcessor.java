
/*-
 * Erp System - Mark VII No 5 (Gideon Series) Server ver 1.5.9
 * Copyright © 2021 - 2023 Edwin Njeru (mailnjeru@gmail.com)
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
package io.github.erp.internal.batch.paymentCategory;

import com.google.common.collect.ImmutableList;
import io.github.erp.internal.framework.batch.ListProcessor;
import io.github.erp.internal.framework.Mapping;
import io.github.erp.internal.model.PaymentCategoryEVM;
import io.github.erp.service.dto.PaymentCategoryDTO;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * List processor implementation that also adds additional data to the output, i.e. the file-processing-token
 */
public class PaymentCategoryPersistenceProcessor implements ListProcessor<PaymentCategoryEVM, PaymentCategoryDTO> {


    private final Mapping<PaymentCategoryEVM, PaymentCategoryDTO> mapping;
    private final String uploadToken;

    @org.jetbrains.annotations.Contract(pure = true)
    public PaymentCategoryPersistenceProcessor(Mapping<PaymentCategoryEVM, PaymentCategoryDTO> mapping, String uploadToken) {
        this.mapping = mapping;
        this.uploadToken = uploadToken;
    }

    @Override
    public List<PaymentCategoryDTO> process(@NonNull List<PaymentCategoryEVM> evms) throws Exception {
        return evms.stream().map(mapping::toValue2).peek(d -> d.setFileUploadToken(uploadToken)).collect(ImmutableList.toImmutableList());
    }
}
