# -*- encoding : utf-8 -*-
# config/initializers/delayed_job_config.rb
# Configuration for delayed_job plugin

Delayed::Worker.destroy_failed_jobs = false
#Delayed::Worker.sleep_delay = 60
Delayed::Worker.max_attempts = 3 # domyślnie jest 25 - to trochę za dużo
Delayed::Worker.max_run_time = 5.minutes