package com.webage.api;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.webage.domain.Event;
import com.webage.repository.EventRepository;

@RestController
@RequestMapping("/events")
public class EventAPI {
	@Autowired
	EventRepository repo;

	@GetMapping
	public Iterable<Event> getAll() {
		//  Workshop:  Implement a method to retrieve all events
		return repo.findAll();
	}

	@GetMapping("/{eventId}")
	public Optional<Event> getEventById(@PathVariable("eventId") long id) {
		//  Workshop:  Implement a method to retrieve a single event by it's ID
		return repo.findById(id);
	}
	
	@PostMapping
	public ResponseEntity<?> addEvent(@RequestBody Event newEvent, UriComponentsBuilder uri) {
		//  Workshop:  Implement a method to create a new event in response to a POST message.
		//  Think about how you ensure that the event is properly formed.
		if(newEvent.getId() != 0 || newEvent.getCode() == null || newEvent.getDescription() == null || newEvent.getTitle() == null) {
			return ResponseEntity.badRequest().build();
		}
		newEvent = repo.save(newEvent);
		URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newEvent.getId()).toUri();
		ResponseEntity<?> response=ResponseEntity.created(location).build();
		return response;
	}

	@PutMapping("/{eventId}")
	public ResponseEntity<?> putEvent(
			@RequestBody Event newEvent,
			@PathVariable("eventId") long eventId) 
	{
		//  Workshop:  Implement a method to update an entitye in response to a PUT message.
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			Event event = repo.findById(eventId).get();
			event.setTitle(newEvent.getTitle());
			event.setCode(newEvent.getCode());
			event.setDescription(newEvent.getDescription());
			repo.save(event);
			map.put("status", 1);
			map.put("data", repo.findById(eventId));
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception ex) {
			map.clear();
			map.put("status", 0);
			map.put("message", "Event is not found");
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		}
	}	
	
	@DeleteMapping("/{eventId}")
	public ResponseEntity<?> deleteEventById(@PathVariable("eventId") long id) {
		//  Workshop:  Implement a method to delete an entity. 
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			repo.deleteById(id);
			map.put("status", 1);
			map.put("message", "Event is deleted successfully!");
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception ex) {
			map.clear();
			map.put("status", 0);
			map.put("message", "Event is not found");
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		}
	}	
	
}
