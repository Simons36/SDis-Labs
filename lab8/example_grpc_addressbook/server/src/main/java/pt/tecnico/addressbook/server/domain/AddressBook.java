package pt.tecnico.addressbook.server.domain;

import pt.tecnico.addressbook.grpc.AddressBookList;
import pt.tecnico.addressbook.grpc.PersonInfo.PhoneNumber;
import pt.tecnico.addressbook.grpc.PersonInfo.PhoneType;
import pt.tecnico.addressbook.server.domain.exception.DuplicatePersonInfoException;
import pt.tecnico.addressbook.grpc.PersonInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;



import io.grpc.StatusRuntimeException;

public class AddressBook {

    private ConcurrentHashMap<String, Person> people = new ConcurrentHashMap<>();

    public AddressBook() {
    }

    public void addPerson(String name, String email, int phoneNumber, PhoneType type, String birthday) throws DuplicatePersonInfoException {
        if(people.putIfAbsent(email, new Person(name, email, phoneNumber, type, birthday)) != null) {
            throw new DuplicatePersonInfoException(email);
        }
    }

    public AddressBookList proto() {
        return AddressBookList.newBuilder()
                .addAllPeople(people.values().stream().map(Person::proto).collect(Collectors.toList()))
                .build();
    }

    public PersonInfo searchPerson(String email){
        Person person = people.get(email);

        return PersonInfo.newBuilder().setEmail(email)
                                      .setName(person.getName())
                                      .setPhone(PhoneNumber.newBuilder().setNumber(person.getPhoneNumber())
                                      .setType(person.getType()))
                                      .build();

    }

    public List<PersonInfo> listAll(String birthday){
        List<PersonInfo> personList = new ArrayList<>();
        
        for(Person person : people.values()){
            if(person.getBirthday().equals(birthday)){
                personList.add(PersonInfo.newBuilder().setEmail(person.getEmail())
                .setName(person.getName())
                .setPhone(PhoneNumber.newBuilder().setNumber(person.getPhoneNumber())
                .setType(person.getType()))
                .setBirthday(birthday)
                .build());
            }
        }
        
        if(personList.isEmpty()){
            throw new RuntimeException("No people with that birthday");
        }

        return personList;
    }
}
