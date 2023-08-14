package in.ajay.controller;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import in.ajay.binding.CustomerEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
public class CustomerRestController {

	//Normal Rest method call
	/*@GetMapping("/event")
	public CustomerEvent getEvent() {
		CustomerEvent event = new CustomerEvent("John", new Date());
		return event;
	}*/
	
	//Mono Rest method call
	@GetMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<CustomerEvent> getEvent() {
		CustomerEvent event = new CustomerEvent("John", new Date());
		return Mono.justOrEmpty(event);
	}
	
	//Flux Rest method call
	@GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<CustomerEvent>> getEvents(){
		
		//Creating Customer data in the form of Object
		CustomerEvent event = new CustomerEvent("Smith", new Date());
		
		//Creating the stream object to send the data
		Stream<CustomerEvent> customerStream = Stream.generate(() -> event);
		
		//Create flux Object with Stream
		Flux<CustomerEvent> customerFlux = Flux.fromStream(customerStream);
		
		//Setting Response interval
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(3));
		
		//Combine Flux Interval and Customer Flux
		Flux<Tuple2<Long, CustomerEvent>> zip = Flux.zip(interval, customerFlux);
		
		// Getting Flux value from the zip
		Flux<CustomerEvent> fluxMap = zip.map(Tuple2::getT2);
		
		//Returning Flux Responses
		return new ResponseEntity(fluxMap, HttpStatus.OK);
	}
}
