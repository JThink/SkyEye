#!/usr/bin/python198
# -*- coding: UTF-8 -*-

import sys
import datetime
from pyelasticsearch import ElasticSearch
from pyelasticsearch import bulk_chunks

def main(argv):
    index = argv[1]
    doc_type = 'log'
    url = []
    urls = argv[2].strip().split(',')
    for u in urls:
        url.append(u)

    es = ElasticSearch(urls = url, timeout = 60, max_retries = 0)
    create_mapping(es, index, doc_type)

def create_mapping(es, index, doc_type):
    mapping = {
        'settings': {
            'index': {
                'number_of_replicas': 1,
                'number_of_shards': 6,
                'refresh_interval': '5s'
            }
        },
        'mappings': {
            '_default_': {
                '_all': {
                    'enabled': False
                }
            },
            doc_type : {
                'properties' : {
                    'created': { 'type': 'date', 'index': 'not_analyzed'},
                    'time': { 'type': 'string', 'index': 'not_analyzed'},
                    'day': { 'type': 'string', 'index': 'not_analyzed'},
                    'week': { 'type': 'string', 'index': 'not_analyzed'},
                    'month': { 'type': 'string', 'index': 'not_analyzed'},
                    'year': { 'type': 'string', 'index': 'not_analyzed'},
                    'app': { 'type': 'string', 'index': 'not_analyzed'},
                    'host': { 'type': 'string', 'index': 'not_analyzed'},
                    'eventType': { 'type': 'string', 'index': 'not_analyzed'},
                    'account': { 'type': 'string', 'index': 'not_analyzed'},
                    'uniqueName': { 'type': 'string', 'index': 'not_analyzed'},
                    'cost': { 'type': 'long', 'index': 'not_analyzed'},
                    'status': { 'type': 'string', 'index': 'not_analyzed'},
                    'messageSmart': { 'type': 'string', 'analyzer': 'ik_smart', 'search_analyzer': 'ik_smart', 'include_in_all': 'true', 'boost': 8},
                    'messageMax': { 'type': 'string', 'analyzer': 'ik_max_word', 'search_analyzer': 'ik_max_word', 'include_in_all': 'true', 'boost': 8}
                }
            }
        }
    }
    es.create_index(index = index, settings = mapping)


if __name__ == '__main__':
    main(sys.argv)