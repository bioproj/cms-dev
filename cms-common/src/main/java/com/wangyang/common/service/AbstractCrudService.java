package com.wangyang.common.service;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import com.wangyang.common.annotation.QueryField;
import com.wangyang.common.enums.CrudType;
import com.wangyang.common.exception.FileOperationException;
import com.wangyang.common.exception.UserException;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.utils.File2Tsv;
import com.wangyang.common.utils.ObjectToCollection;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.enums.Lang;
import com.wangyang.common.repository.BaseRepository;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangyang
 * @date 2021/6/27
 */
public abstract class AbstractCrudService<DOMAIN extends BaseEntity,DOMAINDTO extends BaseEntity,DOMAINVO extends BaseVo, ID extends Serializable> implements ICrudService<DOMAIN,DOMAINDTO,DOMAINVO, ID> {

    @PersistenceContext
    private EntityManager em;
    private final String domainName;
    private final BaseRepository<DOMAIN, ID> repository;
    public AbstractCrudService(BaseRepository<DOMAIN, ID> repository) {
        this.repository = repository;
        Class<DOMAIN> domainClass = (Class<DOMAIN>) fetchType(0);
        domainName = domainClass.getSimpleName();
    }

    @Override
    public List<DOMAIN> listByIds(List<ID> ids) {
        return repository.findAllById(ids);
    }
    @Override
    @Transactional
    public void truncateTable(){
        Entity entity = getInstance().getClass().getAnnotation(Entity.class);
        String name = entity.name();
        Query query = em.createNativeQuery("truncate table "+name);
        query.executeUpdate();
//        Query resetQuery = em.createNativeQuery("ALTER TABLE "+name+" ALTER COLUMN ID RESTART WITH 1");
//        resetQuery.executeUpdate();
    }
    protected Specification<DOMAIN> buildSpecByQuery(DOMAIN baseFileQuery, String keywords, Set<String> sets) {
        return (Specification<DOMAIN>) (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = toPredicate(baseFileQuery,root, query, criteriaBuilder);
            if(sets!=null && sets.size()!=0 && keywords!=null ){
                String likeCondition = String
                        .format("%%%s%%", StringUtils.strip(keywords));
                List<Predicate> orPredicates = new ArrayList<>();
                for (String filed : sets){
                    Predicate predicate = criteriaBuilder
                            .equal(root.get(filed), keywords);
                    orPredicates.add(predicate);
                }
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }
    protected Specification<DOMAIN> buildSpecByQuery(String keywords, Set<String> sets) {
        return (Specification<DOMAIN>) (root, query, criteriaBuilder) ->{
//            List<Predicate> predicates = toPredicate(baseFileQuery,root, query, criteriaBuilder);
            List<Predicate> predicates = new LinkedList<>();
            if(sets!=null && sets.size()!=0 && keywords!=null ){
                String likeCondition = String
                        .format("%%%s%%", StringUtils.strip(keywords));
                List<Predicate> orPredicates = new ArrayList<>();
                for (String filed : sets){
                    Predicate predicate = criteriaBuilder
                            .like(root.get(filed), likeCondition);
                    orPredicates.add(predicate);
                }
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }
    @Transactional
    @Override
    public void importData(List<DOMAIN> entities,int batchSize ) {
        Session session = em.unwrap(Session.class);
//        int batchSize = 100; // 设置批量大小
        for (int i = 0; i < entities.size(); i++) {
            DOMAIN entity = entities.get(i);
            session.persist(entity);
            if (i % batchSize == 0 && i > 0) {
                session.flush();
                session.clear();
            }
        }
    }

    protected List<Predicate> toPredicate(DOMAIN domain,Root<DOMAIN> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new LinkedList<>();
        try {
            List<Field> fields = ObjectToCollection.setConditionFieldList(domain);
            for(Field field : fields){
                boolean fieldAnnotationPresent = field.isAnnotationPresent(QueryField.class);
                if(fieldAnnotationPresent){
                    QueryField queryField = field.getDeclaredAnnotation(QueryField.class);
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object value = field.get(domain);
                    if(value!=null && !value.equals("")){
                        predicates.add(criteriaBuilder.equal(root.get(fieldName),value));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return predicates;
    }



    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "type index must be between 0 to 1");
        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
    }

    @Override
    public DOMAIN findById(ID Id){
        Optional<DOMAIN> fileOptional = repository.findById(Id);
        if(!fileOptional.isPresent()){
            throw new UserException("查找的象不存在!");
        }
        return fileOptional.get();
    }
    @Override
    public List<DOMAIN> listAll() {
//        Assert.notNull(id, domainName + " id must not be null");

        return repository.findAll();
    }

    @Override
    public DOMAIN add(DOMAIN domain) {
//        System.out.println(domainName);
        return repository.save(domain);
    }

    @Override
    public DOMAIN save(DOMAIN domain) {
        return repository.save(domain);
    }

    protected DOMAIN getInstance()
    {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<DOMAIN> type = (Class<DOMAIN>) superClass.getActualTypeArguments()[0];
        try
        {
            return type.newInstance();
        }
        catch (Exception e)
        {
            // Oops, no default constructor
            throw new RuntimeException(e);
        }
    }
    protected DOMAINVO getVOInstance()
    {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<DOMAINVO> type = (Class<DOMAINVO>) superClass.getActualTypeArguments()[2];
        try
        {
            return type.newInstance();
        }
        catch (Exception e)
        {
            // Oops, no default constructor
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(){
        repository.deleteAll();
    }
    @Override
    public void delete(DOMAIN t){
        repository.delete(t);
    }

    @Override
    public Page<DOMAIN> pageBy(Pageable pageable,String keywords,Set<String> sets) {
        return repository.findAll(buildSpecByQuery(keywords,sets),pageable);
    }

    @Override
    public Page<DOMAIN> pageBy(Pageable pageable){
        return repository.findAll(pageable);
    }
    @Override
    public Page<DOMAIN> pageBy(Pageable pageable,DOMAIN baseFileQuery, String keywords,Set<String> sets) {
        return repository.findAll(buildSpecByQuery(baseFileQuery,keywords,sets),pageable);
    }
    @Override
    public void deleteAll(Iterable<DOMAIN> domains){
        repository.deleteAll(domains);
    }

//    @Override
//    public  void   createTSVFile(HttpServletResponse response){
//        List<DOMAIN> domains = listAll();
//        String name = getInstance().getClass().getSimpleName();
//        File tsvFile = createTSVFile(domains,
//                "/export/"+name+".tsv", null);//CacheStore.getValue("workDir")+
//        ServletOutputStream outputStream = null;
//        try {
//            outputStream = response.getOutputStream();
//            byte[] bytes = FileUtils.readFileToByteArray(tsvFile);
//            //写之前设置响应流以附件的形式打开返回值,这样可以保证前边打开文件出错时异常可以返回给前台
//            response.setHeader("content-disposition","attachment;filename="+tsvFile.getName());
//            outputStream.write(bytes);
//            outputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                if(outputStream!=null){
//                    outputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    @Override
    public List<DOMAIN> saveAll(Iterable<DOMAIN> domain) {
        return repository.saveAll(domain);
    }

    @Override
    public File createTSVFile(List<DOMAIN> domains, String filePath,String[] heads){
        File csvFile = new File(filePath);
        File parent = csvFile.getParentFile();
        if (parent != null && !parent.exists())
        {
            parent.mkdirs();
        }
        TsvWriter writer=null;
        Writer fileWriter =null;
        try {
            TsvWriterSettings settings = new TsvWriterSettings();
            BeanWriterProcessor<DOMAIN> beanWriterProcessor = new BeanWriterProcessor<>((Class<DOMAIN>) getInstance().getClass());
            settings.setRowWriterProcessor(beanWriterProcessor);
            fileWriter = new FileWriter(csvFile);
            settings.setHeaderWritingEnabled(true);
            if(heads!=null){
                settings.setHeaders(heads);
            }
            writer = new TsvWriter(fileWriter,settings);

            writer.writeHeaders();
            writer.processRecords(domains);
            return csvFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException(e.getMessage());
        } finally {
            if(writer!=null){
                writer.close();
            }
            try {
                if(fileWriter!=null){
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<DOMAIN> tsvToBean(String filePath){
        return File2Tsv.tsvToBean((Class<DOMAIN>) getInstance().getClass(),filePath);
//        try {
//            try(FileInputStream inputStream = new FileInputStream(filePath)){
//                DOMAIN instance = getInstance();
//                BeanListProcessor<DOMAIN> beanListProcessor = new BeanListProcessor<>((Class<DOMAIN>) getInstance().getClass());
//                TsvParserSettings settings = new TsvParserSettings();
//                settings.setProcessor(beanListProcessor);
//                settings.setHeaderExtractionEnabled(true);
//                TsvParser parser = new TsvParser(settings);
//                parser.parse(inputStream);
//                List<DOMAIN> beans = beanListProcessor.getBeans();
//                inputStream.close();
//                return beans;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }


    @Override
    @Transactional
    public List<DOMAIN> initData(String filePath,Boolean isEmpty){
//        Cache cache = concurrentMapCacheManager.getCache("TERM");
//        cache.clear();
        if(isEmpty){
            truncateTable();
        }
        List<DOMAIN> beans = tsvToBean(filePath);
        if(beans==null){
            throw new FileOperationException(filePath+" 不存在！");
        }
        if(beans.size()!=0){
            beans = repository.saveAll(beans);
        }
        return beans;
    }

    @Override
    public DOMAIN delBy(ID id) {
        DOMAIN domain = findById(id);
        repository.delete(domain);
        return domain;
    }



////    @Override
//    @Override
//    public List<DOMAINVO> listVoTree(List<DOMAINVO> domainvos) {
//        List<DOMAINVO> listWithTree = listWithTree(domainvos);
//        return listWithTree;
//    }

    @Override
    public List<DOMAINVO> listWithTree(List<DOMAINVO> list) {
        return listWithTree(list,0);
    }

    @Override
    public List<DOMAINVO> listWithTree(List<DOMAINVO> list, Integer parentId) {
        // 1. 先查出所有数据
//        List<ProjectLeader> list = projectLeaderService.list(Condition.getLikeQueryWrapper(projectLeader));
        List<DOMAINVO> collect = list.stream()
                // 2. 找出所有顶级（规定 0 为顶级）
                .filter(o -> o.getParentId().equals(parentId))
                // 3.给当前父级的 childList 设置子
                .peek(o -> o.setChildren(getChildList(o, list)))
                .sorted(Comparator.comparing(DOMAINVO::getOrder))
                // 4.收集
                .collect(Collectors.toList());
        return collect;
    }

    // 根据当前父类 找出子类， 并通过递归找出子类的子类
    private List<DOMAINVO> getChildList(DOMAINVO domainvo, List<DOMAINVO> list) {
        return list.stream()
                //筛选出父节点id == parentId 的所有对象 => list
                .filter(o -> o.getParentId().equals(domainvo.getId()))
                .peek(o -> o.setChildren(getChildList(o, list)))
                .sorted(Comparator.comparing(DOMAINVO::getOrder))
                .collect(Collectors.toList());
    }
    @Override
    public void updateOrder(List<DOMAIN> domains,List<DOMAINVO> domainvos) {
        List<DOMAIN> saveDomains = new ArrayList<>();

        Map<Integer, DOMAIN> domainMap = ServiceUtil.convertToMap(domains, DOMAIN::getId);

        updateOrder(domainMap,saveDomains,domainvos,0);
        repository.saveAll(domains);
    }
    @Override
    public void updateOrder(List<DOMAINVO> domainvos) {
        List<DOMAIN> saveDomains = new ArrayList<>();
        List<DOMAIN> domains = repository.findAll();
        Map<Integer, DOMAIN> domainMap = ServiceUtil.convertToMap(domains, DOMAIN::getId);

        updateOrder(domainMap,saveDomains,domainvos,0);
        repository.saveAll(domains);
    }
    //
//
    private void updateOrder(Map<Integer, DOMAIN> domainMap , List<DOMAIN> saveDomains, List<DOMAINVO> domainvos, Integer pId) {

        for(int i=0;i<domainvos.size();i++){
            DOMAINVO domainvo = domainvos.get(i);
            DOMAIN domain = domainMap.get(domainvo.getId());
            domain.setOrder(i);
            domain.setParentId(pId);
            saveDomains.add(domain);

            List<DOMAINVO> childCategories = domainvo.getChildren();
            if(childCategories.size()>0){
                updateOrder(domainMap,saveDomains,childCategories,domain.getId());
            }

        }
    }

    @Override
    public List<DOMAIN> findByParentId(Integer parentId) {
       return repository.findAll(new Specification<DOMAIN>() {
            @Override
            public Predicate toPredicate(Root<DOMAIN> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("parentId"),parentId)).orderBy(criteriaBuilder.asc(root.get("order"))).getRestriction();

            }
        });
    }



    @Override
    public List<DOMAINVO> convertToListVo(List<DOMAIN> domains) {
        return domains.stream().map(domain -> {
            DOMAINVO domainvo = getVOInstance();
            BeanUtils.copyProperties(domain,domainvo);
            return domainvo;

        }).collect(Collectors.toList());
    }

    @Override
    public List<DOMAIN> listByIds(Set<ID> ids) {
        return repository.findAllById(ids);
    }


    @Override
    public Page<DOMAIN> pageByIds(Set<ID> ids,Integer page,Integer size,Sort sort) {
        if(sort==null){
            sort = Sort.by(Sort.Direction.DESC, "createDate");
        }
        Page<DOMAIN> domains = repository.findAll(new Specification<DOMAIN>() {
            @Override
            public Predicate toPredicate(Root<DOMAIN> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(root.get("id").in(ids)).getRestriction();
            }
        }, PageRequest.of(page, size, sort));
        return domains;
    }
    @Override
    public void addChild(List<DOMAINVO> domainvos, Integer id){
        List<DOMAIN> domains = findByParentId(id);
        if(domains.size()==0){
            return;
        }
        domainvos.addAll(convertToListVo(domains));
        if(domains.size()!=0){
            for (DOMAIN domain:domains){
                addChild(domainvos,domain.getId());
            }
        }

    }

    @Override
    public List<DOMAINVO> getAllChild(Integer id){
        List<DOMAINVO> domainvos =new ArrayList<>();
        addChild(domainvos,id);
        return domainvos;


    }

    @Override
    public DOMAIN findByLang(Integer langSource, Lang lang){
        List<DOMAIN> domains = repository.findAll(new Specification<DOMAIN>() {
            @Override
            public Predicate toPredicate(Root<DOMAIN> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return  query.where(criteriaBuilder.equal(root.get("langSource"),langSource),criteriaBuilder.equal(root.get("lang"),lang)).getRestriction();
            }
        });
        if (domains.size()==0) return null;
        return domains.get(0);

    }

    @Override
    public DOMAIN update(ID id, DOMAIN updateDomain) {
//        System.out.println(domainName);
        DOMAIN domain = findById(id);
        BeanUtils.copyProperties(updateDomain, domain,"id");
        return repository.save(domain);
    }

    @Override
    public boolean supportType(CrudType type) {
        return false;
    }
}
