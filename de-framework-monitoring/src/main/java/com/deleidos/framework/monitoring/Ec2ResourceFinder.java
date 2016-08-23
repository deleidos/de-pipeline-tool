package com.deleidos.framework.monitoring;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

public enum Ec2ResourceFinder {

	instance;

	public String lookupPrivateIp(String tagKey, String tagValue) {
		String privateIp = "unknown";

		AmazonEC2 client = null;
		try {
			client = new AmazonEC2Client(new DefaultAWSCredentialsProviderChain());
			Filter filters = new Filter(tagKey).withValues(tagValue);

			DescribeInstancesRequest dir = new DescribeInstancesRequest().withFilters(filters);

			DescribeInstancesResult rslt = client.describeInstances(dir);

			outerloop: for (Reservation reservation : rslt.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					for (Tag tag : instance.getTags()) {
						if (tag.getKey().equals(tagKey.split(":")[1])) {
							privateIp = instance.getPrivateIpAddress();
							break outerloop;
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO add/use a logger
			e.printStackTrace();
		} finally {
			if (client != null)
				client.shutdown();
		}

		return privateIp;
	}

	public String lookupPublicIp(String tagKey, String tagValue) {
		String privateIp = "unknown";

		AmazonEC2 client = null;
		try {
			client = new AmazonEC2Client(new DefaultAWSCredentialsProviderChain());
			Filter filters = new Filter(tagKey).withValues(tagValue);

			DescribeInstancesRequest dir = new DescribeInstancesRequest().withFilters(filters);

			DescribeInstancesResult rslt = client.describeInstances(dir);

			outerloop: for (Reservation reservation : rslt.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					for (Tag tag : instance.getTags()) {
						if (tag.getKey().equals(tagKey.split(":")[1])) {
							privateIp = instance.getPublicIpAddress();
							break outerloop;
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO add/use a logger
			e.printStackTrace();
		} finally {
			if (client != null)
				client.shutdown();
		}

		return privateIp;
	}
}
