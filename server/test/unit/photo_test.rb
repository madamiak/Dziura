# -*- encoding : utf-8 -*-

require 'test_helper'

class PhotoTest < ActiveSupport::TestCase

  def setup
    # PNG, 10x10
    @png_image = "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAIAAAACUFjqAAAAAXNSR0IArs4c\n" +
                 "6QAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9wEExEnLZz6DxIAAAAZ\n" +
                 "dEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAALElEQVQY02P8\n" +
                 "//8/A27AxIAXsEAoRkZGCAPNMAK6CUgz4ncaCxYdSO6gqd0Amn8PDQbGRccA\n" +
                 "AAAASUVORK5CYII=\n"
    # JPG, 10x10
    @jpg_image = "/9j/4AAQSkZJRgABAQIAHAAcAAD//gATQ3JlYXRlZCB3aXRoIEdJTVD/2wBD\n" +
                 "ABALDA4MChAODQ4SERATGCgaGBYWGDEjJR0oOjM9PDkzODdASFxOQERXRTc4\n" +
                 "UG1RV19iZ2hnPk1xeXBkeFxlZ2P/wAALCAAKAAoBAREA/8QAFwAAAwEAAAAA\n" +
                 "AAAAAAAAAAAAAAIFBv/EACAQAAMAAgEFAQEAAAAAAAAAAAECAwQRBQASITFB\n" +
                 "MpH/2gAIAQEAAD8A02RfN4zkszkOSzpLwizUTmEJdHJUbOl377vp9/ytj3nk\n" +
                 "48siLd0qoHRta2CNg+ei8I5MWjkSnaTfpKKGU/fIPTTmkprOaKiIAqqo0FA9\n" +
                 "ADr/2Q==\n"
  end

  test "valid image and markers" do

    p1 = Photo.create(:photo => @png_image, :mime_type => "image/png")
    assert(p1.valid?)

    p2 = Photo.create(:photo => @jpg_image, :mime_type => "image/jpeg")
    assert(p2.valid?)

    p1.markers.build [ :x => 5, :y => 5 ]
    assert(p1.valid?)

  end

  test "invalid image data" do

    p = Photo.create(:photo => "bla bla bla", :mime_type => "image/jpeg")
    assert(! p.valid?)

  end

  test "invalid markers" do

    p1 = Photo.create(:photo => @png_image, :mime_type => "image/png")
    p1.markers.build [ :x => 0, :y => 1 ]
    assert(! p1.valid?)

    p1 = Photo.create(:photo => @png_image, :mime_type => "image/png")
    p1.markers.build [ :x => 10, :y => 11 ]
    assert(! p1.valid?)

  end

end
