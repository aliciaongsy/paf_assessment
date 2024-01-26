package vttp2023.batch4.paf.assessment.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 * db.listings.aggregate([
		{
			$unwind: "$address"
		},
		{
			$group: {
				_id: "$address.suburb"
			}
		}
		]);
	 *
	 */
	public List<String> getSuburbs(String country) {
		UnwindOperation unwind = Aggregation.unwind("address");

		GroupOperation group = Aggregation.group("address.suburb");

		Aggregation pipeline = Aggregation.newAggregation(unwind, group);

		AggregationResults<Document> results = template.aggregate(pipeline,"listings", Document.class);

		List<Document> docs = results.getMappedResults();

		List<String> suburbs = docs.stream().filter(d -> !d.getString("_id").equals("")).map(d -> d.getString("_id")).toList();

		return suburbs;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *db.listings.aggregate([
		{ $match: {
			$and: [{price: {$lte: 300}}, {"address.suburb": "Darlinghurst"}, {accommodates: {$gte: 2}}, {min_nights: { $lte: 2}} ]
		}},{
			$project: {
				_id: 1,
				name: 1,
				accommodates: 1,
				price: 1
			}
		}
		]);
	 *
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		MatchOperation match = Aggregation.match(Criteria.where("address.suburb").is(suburb)
			.andOperator(Criteria.where("accommodates").gte(persons),
				Criteria.where("min_nights").lte(duration),
				Criteria.where("price").lte(priceRange)));

		ProjectionOperation project = Aggregation.project("_id", "name", "accommodates", "price");

		Aggregation pipeline = Aggregation.newAggregation(match, project);

		AggregationResults<Document> results = template.aggregate(pipeline,"listings", Document.class);

		List<Document> list = results.getMappedResults();

		List<AccommodationSummary> accsSummaries = new LinkedList<>();
		for(Document d:list){
			AccommodationSummary summary = new AccommodationSummary();
			summary.setId(d.getString("_id"));
			summary.setName(d.getString("name"));
			summary.setAccomodates(d.getInteger("accommodates"));
			summary.setPrice(d.get("price", Number.class).floatValue());
			accsSummaries.add(summary);
		}
		return accsSummaries;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
