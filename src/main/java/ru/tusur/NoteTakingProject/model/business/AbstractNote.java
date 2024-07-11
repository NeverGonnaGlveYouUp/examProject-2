//package ru.tusur.NoteTakingProject.model.business;
//
//
//import io.hypersistence.utils.hibernate.type.json.JsonType;
//import jakarta.persistence.*;
//import lombok.Data;
//import org.hibernate.annotations.Type;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Entity
//@Data
//public abstract class AbstractNote {
//    @Id
//    @Column(name = "id", nullable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Type(JsonType.class)
//    @Column(columnDefinition = "jsonb")
//    private final Map<String, String> properties = new HashMap<>();
//
//    @Column
//    private List<AbstractNote> links;
//}
