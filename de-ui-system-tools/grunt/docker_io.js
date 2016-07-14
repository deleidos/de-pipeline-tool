/*
 * grunt-docker-io
 * https://github.com/drGrove/grunt-docker-io
 *
 * Copyright (c) 2015 Danny Grove
 * Licensed under the AGPL license.
 */

'use strict';

var spawn = require('child_process').spawn
var fs = require('fs')

module.exports = function(grunt) {

  // Please see the Grunt documentation for more information regarding task
  // creation: http://gruntjs.com/creating-tasks

  grunt.registerMultiTask('docker_io', 'Build and Push Docker Images', function() {
    // Merge task-specific and/or target-specific options with these defaults.
    var DOCKER_HUB_URL = "http://hub.docker.io"
    var REQUIRES_LOGIN = "Please login prior to push:"
    var opts = this.options({
      dockerFileLocation: '.',
      buildName: '',
      tag: ['latest'],
      pushLocation: DOCKER_HUB_URL,
      username: process.env.USER,
      push: true,
      force: false
    });

    var done = this.async();
    var queue = [];
    var next = function() {
      if(!queue.length) {
        return done()
      }
      queue.shift()();
    }

    var runIf = function(condition, behavior){
      if(condition) {
        queue.push(behavior)
      }
    }

    var getBase = function(){
      var buildName
      if(opts.pushLocation === DOCKER_HUB_URL) {
        buildName = opts.username + '/' + opts.buildName
      } else {
        buildName = opts.pushLocation + '/' + opts.buildName
      }
      return buildName;
    }

    // Check that user is logged in
    runIf(opts.push, function(){
      var loginOpts = ['login']
      if(opts.pushLocation !== DOCKER_HUB_URL) {
        loginOpts.push(opts.pushLocation)
      }

      var dockerLogin = spawn('docker', loginOpts)
      dockerLogin.stdout.on('data', function(data){
        data = data || ''
        var usernameRegex = /\(.*\)/
        if(usernameRegex.exec(data) && usernameRegex.exec(data).length > 0) {
          if(usernameRegex.exec(data)[0] !== '(' + opts.username + ')'){
            grunt.fatal('Please Login First')
          }
          next()
        } else {
          grunt.fatal('Please login to the docker registry - ' + opts.pushLocation)
        }
      })
    })

    if( typeof opts.tag === 'string')
      opts.tag = opts.tag.split(',')
    if(opts.tag === '' || opts.tag === [] || opts.tag === 'latest') {
      opts.tag = [];
      opts.tag.push('latest')
    }
    var tagCount = opts.tag.length;
    for(var i = 0; i < tagCount; i++) {
      runIf
      ( opts.dockerFileLocation !== ''
        && opts.buildName !== ''
      , function(i){
          var buildOpts = ['build']
          var buildName = getBase();
          buildOpts.push('-t')
          buildOpts.push(buildName + ':' + opts.tag[0])
          buildOpts.push(opts.dockerFileLocation)
          console.log(buildOpts.join(' '))
          var dockerBuild = spawn('docker', buildOpts)
          dockerBuild.stdout.on('data', function(data){
            grunt.log.ok(data)
          })
          dockerBuild.stderr.on('data', function(data){
            grunt.fatal('Could not build image - ' +  data)
          })
          dockerBuild.on('exit', function(code){
            if(code === 0) {
              opts.tag.shift()
              next()
            } else {
              grunt.fatal('Error Building image')
            }
          })
        }
      )
    }
    runIf(opts.push, function(){
      var pushOpts = []
      pushOpts.push('push')
      pushOpts.push(getBase())
      var dockerPush = spawn('docker', pushOpts)
      dockerPush.stdout.on('data', function(data){
        if(data === REQUIRES_LOGIN) {
          grunt.fatal('You must first login ')
        }
        grunt.log.ok(data)
      })
      dockerPush.stderr.on('data', function(data){
        grunt.log.error(data)
      })
      dockerPush.on('exit', function(code){
        if(code !== 0) {
          grunt.fatal('Could not push docker image ' + opts.buildName)
        }
        next()
      })
    })

    next()
  });
};
