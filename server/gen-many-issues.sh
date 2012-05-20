#!/bin/bash

# Skrypt do generowania wielu zgłoszeń

rails console <<< '
# 400 takich samych instancji
for i in 1..400 do
  Issue.add_issue(1, "17.04126", "51.11744", "foo #{i}", nil, nil)
end

# 400 zgłoszeń losowo w prostokącie
latMin = BigDecimal.new("51.11839")
latMax = BigDecimal.new("51.12319")
lngMin = BigDecimal.new("17.02911")
lngMax = BigDecimal.new("17.03908")
for i in 1..400 do
  rnd_lat = (latMin + (latMax - latMin) * rand).to_s
  rnd_lng = (lngMin + (lngMax - lngMin) * rand).to_s
  Issue.add_issue(2, rnd_lng, rnd_lat, "bar #{i}", nil, nil)
end
quit'