package io.github.erp.service;

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

import io.github.erp.domain.*; // for static metamodels
import io.github.erp.domain.AmortizationPostingReportRequisition;
import io.github.erp.repository.AmortizationPostingReportRequisitionRepository;
import io.github.erp.repository.search.AmortizationPostingReportRequisitionSearchRepository;
import io.github.erp.service.criteria.AmortizationPostingReportRequisitionCriteria;
import io.github.erp.service.dto.AmortizationPostingReportRequisitionDTO;
import io.github.erp.service.mapper.AmortizationPostingReportRequisitionMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AmortizationPostingReportRequisition} entities in the database.
 * The main input is a {@link AmortizationPostingReportRequisitionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AmortizationPostingReportRequisitionDTO} or a {@link Page} of {@link AmortizationPostingReportRequisitionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AmortizationPostingReportRequisitionQueryService extends QueryService<AmortizationPostingReportRequisition> {

    private final Logger log = LoggerFactory.getLogger(AmortizationPostingReportRequisitionQueryService.class);

    private final AmortizationPostingReportRequisitionRepository amortizationPostingReportRequisitionRepository;

    private final AmortizationPostingReportRequisitionMapper amortizationPostingReportRequisitionMapper;

    private final AmortizationPostingReportRequisitionSearchRepository amortizationPostingReportRequisitionSearchRepository;

    public AmortizationPostingReportRequisitionQueryService(
        AmortizationPostingReportRequisitionRepository amortizationPostingReportRequisitionRepository,
        AmortizationPostingReportRequisitionMapper amortizationPostingReportRequisitionMapper,
        AmortizationPostingReportRequisitionSearchRepository amortizationPostingReportRequisitionSearchRepository
    ) {
        this.amortizationPostingReportRequisitionRepository = amortizationPostingReportRequisitionRepository;
        this.amortizationPostingReportRequisitionMapper = amortizationPostingReportRequisitionMapper;
        this.amortizationPostingReportRequisitionSearchRepository = amortizationPostingReportRequisitionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link AmortizationPostingReportRequisitionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AmortizationPostingReportRequisitionDTO> findByCriteria(AmortizationPostingReportRequisitionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AmortizationPostingReportRequisition> specification = createSpecification(criteria);
        return amortizationPostingReportRequisitionMapper.toDto(amortizationPostingReportRequisitionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AmortizationPostingReportRequisitionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AmortizationPostingReportRequisitionDTO> findByCriteria(
        AmortizationPostingReportRequisitionCriteria criteria,
        Pageable page
    ) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AmortizationPostingReportRequisition> specification = createSpecification(criteria);
        return amortizationPostingReportRequisitionRepository
            .findAll(specification, page)
            .map(amortizationPostingReportRequisitionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AmortizationPostingReportRequisitionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AmortizationPostingReportRequisition> specification = createSpecification(criteria);
        return amortizationPostingReportRequisitionRepository.count(specification);
    }

    /**
     * Function to convert {@link AmortizationPostingReportRequisitionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AmortizationPostingReportRequisition> createSpecification(
        AmortizationPostingReportRequisitionCriteria criteria
    ) {
        Specification<AmortizationPostingReportRequisition> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AmortizationPostingReportRequisition_.id));
            }
            if (criteria.getRequestId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getRequestId(), AmortizationPostingReportRequisition_.requestId));
            }
            if (criteria.getTimeOfRequisition() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getTimeOfRequisition(), AmortizationPostingReportRequisition_.timeOfRequisition)
                    );
            }
            if (criteria.getFileChecksum() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(criteria.getFileChecksum(), AmortizationPostingReportRequisition_.fileChecksum)
                    );
            }
            if (criteria.getTampered() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getTampered(), AmortizationPostingReportRequisition_.tampered));
            }
            if (criteria.getFilename() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getFilename(), AmortizationPostingReportRequisition_.filename));
            }
            if (criteria.getReportParameters() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(criteria.getReportParameters(), AmortizationPostingReportRequisition_.reportParameters)
                    );
            }
            if (criteria.getAmortizationPeriodId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAmortizationPeriodId(),
                            root ->
                                root
                                    .join(AmortizationPostingReportRequisition_.amortizationPeriod, JoinType.LEFT)
                                    .get(AmortizationPeriod_.id)
                        )
                    );
            }
            if (criteria.getRequestedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRequestedById(),
                            root -> root.join(AmortizationPostingReportRequisition_.requestedBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
            if (criteria.getLastAccessedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLastAccessedById(),
                            root -> root.join(AmortizationPostingReportRequisition_.lastAccessedBy, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
