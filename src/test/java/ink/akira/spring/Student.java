package ink.akira.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private String name;
    private List<Pet> pets;
    private Map<String, Pet> mappedPets;
}
