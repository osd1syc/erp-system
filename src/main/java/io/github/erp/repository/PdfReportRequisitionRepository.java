package io.github.erp.repository;

/*-
 * Erp System - Mark IV No 6 (Ehud Series) Server ver 1.4.0
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
import io.github.erp.domain.PdfReportRequisition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PdfReportRequisition entity.
 */
@Repository
public interface PdfReportRequisitionRepository
    extends JpaRepository<PdfReportRequisition, Long>, JpaSpecificationExecutor<PdfReportRequisition> {
    @Query(
        value = "select distinct pdfReportRequisition from PdfReportRequisition pdfReportRequisition left join fetch pdfReportRequisition.placeholders left join fetch pdfReportRequisition.parameters",
        countQuery = "select count(distinct pdfReportRequisition) from PdfReportRequisition pdfReportRequisition"
    )
    Page<PdfReportRequisition> findAllWithEagerRelationships(Pageable pageable);

    @Query(
        "select distinct pdfReportRequisition from PdfReportRequisition pdfReportRequisition left join fetch pdfReportRequisition.placeholders left join fetch pdfReportRequisition.parameters"
    )
    List<PdfReportRequisition> findAllWithEagerRelationships();

    @Query(
        "select pdfReportRequisition from PdfReportRequisition pdfReportRequisition left join fetch pdfReportRequisition.placeholders left join fetch pdfReportRequisition.parameters where pdfReportRequisition.id =:id"
    )
    Optional<PdfReportRequisition> findOneWithEagerRelationships(@Param("id") Long id);
}
