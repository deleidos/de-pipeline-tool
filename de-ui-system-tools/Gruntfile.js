/*global module:false*/
module.exports = function (grunt) {

    require('jit-grunt')(grunt, {
        useminPrepare: 'grunt-usemin',
        docker_io: 'grunt/docker_io.js'
    });
    require('time-grunt')(grunt);

    // Project configuration.
    grunt.initConfig({
        // Metadata
        pkg: grunt.file.readJSON('package.json'),
        banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
        '<%= grunt.template.today("yyyy-mm-dd") %> */\n',
        meta: {
            app_files: ['app/**/*.js', '!app/bower_components/**/*'],
            lib_files: ['app/assets/lib/**/*'],
            dist_dir:  'dist',
            temp_dir: '.tmp'
        },

        // Task configuration
        connect: {
            server: {
                options: {
                    port: 9000,
                    base: 'app',
                    open: true,
                    hostname: 'localhost',
                    livereload: true
                }
            },
            dist: {
                options: {
                    base: '<%= meta.dist_dir %>',
                    open: true
                }
            }
        },
        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: 'app',
                    dest: '<%= meta.dist_dir %>',
                    filter: 'isFile',
                    src: [
                        '**/*',
                        '!**/*.js',
                        '!**/*.css',
                        '!bower_components/**/*'
                    ]
                }]
            }
        },
        clean: {
            dist: '<%= meta.dist_dir %>',
            temp: '<%= meta.temp_dir %>',
            coverage: 'coverage'
        },
        docker_io: {
            deploy: {
                options: {
                    dockerFileLocation: '.',
                    buildName: '<%= pkg.name %>',
                    tag: grunt.option('tag') || 'latest',
                    pushLocation: 'der.deleidos.com',
                    username: 'digitaledge', // Current logged in user
                    push: true
                }
            },
            local: {
                options: {
                    dockerFileLocation: '.',
                    buildName: '<%= pkg.name %>',
                    pushLocation: 'der.deleidos.com',
                    tag: 'latest',
                    push: false
                }
            }
        },
        htmlmin: {
            dist: {
                options: {
                    collapseWhitespace: true,
                    collapseBooleanAttributes: true,
                    removeCommentsFromCDATA: true,
                    removeOptionalTags: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= meta.dist_dir %>',
                    src: ['**/*.html'],
                    dest: '<%= meta.dist_dir %>'
                }]
            }
        },
        jshint: {
            options: {
                jshintrc: true,
                reporterOutput: ""
            },
            gruntfile: {
                src: 'Gruntfile.js'
            },
            app_files: {
                src: '<%= meta.app_files %>'
            }
        },
        jscs: {
            options: {
                config: '.jscsrc',
                fix: true
            },
            src: '<%= meta.app_files %>'
        },
        karma: {
            all: {
                configFile: 'karma.conf.js',
                singleRun: true
            },
            chrome: {
                configFile: 'karma.conf.js',
                singleRun: true,
                browsers: ['Chrome']
            },
            firefox: {
                configFile: 'karma.conf.js',
                singleRun: true,
                browsers: ['Firefox']
            },
            ie: {
                configFile: 'karma.conf.js',
                singleRun: true,
                browsers: ['IE']
            },
            phantomjs: {
                configFile: 'karma.conf.js',
                singleRun: true,
                browsers: ['PhantomJS']
            }
        },
        ngAnnotate: {
            options: {
                singleQuotes: true
            },
            app_files: {
                files: [{
                    expand: true,
                    src: '<%= meta.temp_dir %>/concat/js/<%= pkg.name %>.js'
                }]
            }
        },
        usemin: {
            html: '<%= meta.dist_dir %>/index.html'
        },
        useminPrepare: {
            html: 'app/index.html',
            options: {
                dest: '<%= meta.dist_dir %>',
                staging: '<%= meta.temp_dir %>'
            }
        },
        watch: {
            livereload: {
                files: ['<%= meta.app_files %>', 'app/**/*.html', 'app/**/*.css'],
                options: {
                    livereload: true
                }
            },
            gruntfile: {
                files: '<%= jshint.gruntfile.src %>',
                tasks: ['jshint:gruntfile']
            },
            jshint: {
                files: '<%= meta.app_files %>',
                tasks: ['newer:jshint:app_files', 'newer:jscs']
            }
        },
        wiredep: {
            app: {
                src: ['app/index.html']
            },
            test: {
                src: ['karma.conf.js'],
                devDependencies: true
            }
        },
        uglify: {
            options: {
                beautify: true
            }
        }
    });

    grunt.registerTask('serve', function (target) {
        if (target === 'dist') {
            return grunt.task.run('connect:dist:keepalive');
        }

        grunt.task.run(['wiredep', 'connect:server', 'watch:livereload']);
    });

    grunt.registerTask('test', function (target) {
        grunt.task.run(['clean:coverage', 'wiredep:test', 'karma:' + (target ? target : 'all')]);
    });

    grunt.registerTask('check-code', ['newer:jshint', 'newer:jscs']);

    grunt.registerTask('build', [
        'clean:dist',
        'wiredep:app',
        'useminPrepare',
        'concat',
        'ngAnnotate',
        'copy:dist',
        'cssmin',
        'uglify',
        'usemin',
        'clean:temp'
    ]);

    grunt.registerTask('default', [
        'newer:jshint',
        'newer:jscs',
        'test:phantomjs',
        'build'
    ]);
};
