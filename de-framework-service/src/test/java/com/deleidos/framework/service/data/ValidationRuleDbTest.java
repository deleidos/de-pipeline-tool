package com.deleidos.framework.service.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.deleidos.framework.model.system.ValidationRule;

/**
 * Test validation rule database operations.
 * 
 * @author vernona
 */
public class ValidationRuleDbTest extends DataTestBase {

	@Test
	public void testValidationRuleDb() {
		ValidationRule rule1 = new ValidationRule(UUID.randomUUID().toString(), "TestRule1", "textInput", false, ".+",
				null, null);
		manager.insertValidationRule(rule1);
		ValidationRule rule1a = manager.getValidationRule(rule1.get_id());
		System.out.println(gson.toJson(rule1a));
		assertEquals(rule1.getName(), rule1a.getName());
		assertEquals(rule1.getType(), rule1a.getType());
		assertEquals(rule1.getList(), rule1a.getList());

		List<String> options = new ArrayList<String>();
		options.add("option1");
		options.add("option2");
		options.add("option3");
		ValidationRule rule2 = new ValidationRule(UUID.randomUUID().toString(), "TestRule2", "textInput", false, ".+",
				options, "options.csv");
		manager.insertValidationRule(rule2);
		ValidationRule rule2a = manager.getValidationRule(rule2.get_id());
		System.out.println(gson.toJson(rule2a));
		assertEquals(rule2.getName(), rule2a.getName());
		assertEquals(rule2.getType(), rule2a.getType());
		assertEquals(rule2.getList(), rule2a.getList());
		assertEquals(rule2.getFile(), rule2a.getFile());
		assertEquals(rule2.getOptions().size(), rule2a.getOptions().size());

		List<ValidationRule> rules = manager.getValidationRules();
		assertTrue(rules.size() >= 2);
		for (ValidationRule rule : rules) {
			System.out.println(rule);
		}
		
		manager.deleteValidationRule(rule1.get_id());
		manager.deleteValidationRule(rule2.get_id());
		assertNull(manager.getValidationRule(rule1.get_id()));
		assertNull(manager.getValidationRule(rule2.get_id()));
		
		rules = manager.getValidationRules();
		for (ValidationRule rule : rules) {
			assertFalse(rule.get_id().equals(rule1.get_id()));
			assertFalse(rule.get_id().equals(rule2.get_id()));
		}

	}
}
