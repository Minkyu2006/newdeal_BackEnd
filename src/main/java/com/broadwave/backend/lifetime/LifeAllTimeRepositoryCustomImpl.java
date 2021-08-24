package com.broadwave.backend.lifetime;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

/**
 * @author Minkyu
 * Date : 2021-08-06
 * Remark :
 */
@Repository
public class LifeAllTimeRepositoryCustomImpl extends QuerydslRepositorySupport implements LifeAllTimeRepositoryCustom {

    public LifeAllTimeRepositoryCustomImpl() {
        super(LifeAllTime.class);
    }

    @Override
    public LifeAllTimeDto findById(Long id) {

        QLifeAllTime lifeAllTime = QLifeAllTime.lifeAllTime;

        JPQLQuery<LifeAllTimeDto> query = from(lifeAllTime)
                .select(Projections.constructor(LifeAllTimeDto.class,
                        lifeAllTime.ltBridgeCode,
                        lifeAllTime.ltBridgeName,
                        lifeAllTime.ltSpanNum,
                        lifeAllTime.ltAbsenceCode,

                        lifeAllTime.ltDamageBRank,
                        lifeAllTime.ltDamageCRank,
                        lifeAllTime.ltDamageDRank,
                        lifeAllTime.ltDamageERank,

                        lifeAllTime.ltAllVolume,
                        lifeAllTime.ltDiscountRate,
                        lifeAllTime.ltIncrease,

                        lifeAllTime.ltPeriodicFrequency,
                        lifeAllTime.ltPeriodicCost,
                        lifeAllTime.ltCloseFrequency,
                        lifeAllTime.ltCloseCost,
                        lifeAllTime.ltSafetyFrequency,
                        lifeAllTime.ltSafetyCost
                ));

        query.where(lifeAllTime.id.eq(id));

        return query.fetchOne();
    }

}