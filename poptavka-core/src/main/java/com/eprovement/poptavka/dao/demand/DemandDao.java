/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eprovement.poptavka.dao.demand;

import com.eprovement.poptavka.dao.GenericDao;
import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.common.ResultCriteria;
import com.eprovement.poptavka.domain.demand.Category;
import com.eprovement.poptavka.domain.demand.Demand;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Excalibur
 */
public interface DemandDao extends GenericDao<Demand> {


    /**
     * Load all demands for given localities while applying additional criteria if they are specified.
     *
     * <p>This methods works recursively, that means that really ALL demands
     * for given localities are returned - NOT ONLY demands directly assigned to those localities!
     * <p>
     * E.g.
     * Let be
     *
     * <div>
     * <pre>
     * + loc1
     *   + loc11
     *   + loc12
     *      + 121
     * + loc2
     * </pre>
     * </div>
     *
     * the hierarchy of localities. Then <code>getDemands(loc1)</code> must return all demands related to the following
     * localities: loc1, loc11, loc12 and loc121.
     *
     * @param localities collection of all localities for which we want return (posisibly all) demands that are related
     * to those localities
     * @param resultCriteria additional criteria that will be applied to the found demands, can be null
     * @return all demands that (directly OR indirectly) belongs to the some from given <code>localities</code>.
     * @throws IllegalStateException if <code>resultCriteria</code> specifies order by columns,
     *     see {@link ResultCriteria#orderByColumns}
     */
    Set<Demand> getDemands(Locality[] localities, ResultCriteria resultCriteria);


    /**
     * Optmized method for loading demands count for all localities in one query!
     *
     * @return list of maps, each map containing only 2 items ("locality" => locality, "demandsCount" => demandsCount)
     */
    List<Map<String, Object>> getDemandsCountForAllLocalities();


    /**
     * Get count of ALL demands associated to the some locality from given <code>localities</code>
     *
     * <p>
     * See {@link #getDemands(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)}
     * for further explanation.
     *
     * @param localities
     * @return
     */
    long getDemandsCount(Locality... localities);

    /**
     * Similar to the {@link #getDemandsCount(com.eprovement.poptavka.domain.address.Locality...)}
     * but with better performance.
     * This includes restriction to the one locality.
     *
     * @param locality
     * @return
     */
    long getDemandsCountQuick(Locality locality);

    /**
     * Get count of all demands that belongs directly to the specified locality. No demands belonging to
     * any sublocality are included!
     */
    long getDemandsCountWithoutChildren(Locality locality);


    /**
     * Optmized method for loading demands count for all categories in one query!
     *
     * @return list of maps, each map containing only 2 items ("category" => category, "demandsCount" => demandsCount)
     */
    List<Map<String, Object>> getDemandsCountForAllCategories();

    /**
     * Load all demands for given categories while applying additional criteria <code>resultCriteria</code>
     * if they are specified.
     *
     * <p>
     *     This methods works recursively, that means that really ALL demands
     * for given categories are returned - NOT ONLY demands directly assigned to those categories!
     *
     *
     * @param resultCriteria
     * @param categories
     * @return all demands that (directly OR indirectly) belongs to the some from given <code>localities</code>.
     * @throws IllegalStateException if <code>resultCriteria</code> specifies order by columns
     *
     * @see #getDemands(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)
     * @see ResultCriteria#orderByColumns
     */
    Set<Demand> getDemands(Category[] categories, ResultCriteria resultCriteria);

    /**
     * Get count of ALL demands associated to the some category from given <code>categories</code>.
     *
     * @param categories
     * @return
     *
     * @see #getDemands(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)
     */
    long getDemandsCount(Category... categories);

    /**
     * Similar to the {@link #getDemandsCount(com.eprovement.poptavka.domain.demand.Category...)}
     * but with better performance.
     * This includes restriction to the one category.
     *
     * @param category
     * @return
     */
    long getDemandsCountQuick(Category category);

    /**
     * Get count of all demands that belongs directly to the specified category. No demands belonging to
     * any subcategory are included!
     */
    long getDemandsCountWithoutChildren(Category category);



    /**
     * Returns the count of all demands in DB.
     * @return
     */
    long getAllDemandsCount();


    /**
     * Load all demands that are new, i.e. have status {@link com.eprovement.poptavka.domain.demand.DemandStatus#NEW}
     * @return
     * @param resultCriteria
     */
    List<Demand> getAllNewDemands(ResultCriteria resultCriteria);


    /**
     * Load all demands associated to the given category (-ies) and
     * category (-ies) - each must be associated to both - while applying
     * additional criteria <code>resultCriteria</code> if they are specified.
     *
     * @param categories
     * @param localities
     * @param resultCriteria
     * @return collection of demands that are related to the given localities and adher to <code>resultCriteria</code>
     * @throws IllegalStateException if <code>resultCriteria</code> specifies order by columns
     */
    Set<Demand> getDemands(Category[] categories, Locality[] localities, ResultCriteria resultCriteria);


     /**
     * Evaluates the number of demands associated to the given
     * <code>locality</code>(-ies) and <code>category</code>(-ies).
     * <p>
     * Use this method instead of {@link #getDemands(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)} if you want
     * to retrieve only number of suppliers - this method is far more lightweight than usage of
     * {@link #getDemands(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)}
     *      .size().
     *
     * @param localities
     * @return number of suppliers related to the <code>locality</code>(-ies).
     */

    long getDemandsCount(Category[] categories, Locality[] localities,
            ResultCriteria resultCriteria);
}