# -*- encoding : utf-8 -*-
require 'test_helper'

class IssueInstanceTest < ActiveSupport::TestCase

  test "e-mail verification" do
    i1 = IssueInstance.create(:notificar_email => 'zly adres')
    assert(! i1.errors.messages[:notificar_email].nil?)

    i2 = IssueInstance.create(:notificar_email => 'zly@adres')
    assert(! i2.errors.messages[:notificar_email].nil?)

    i3 = IssueInstance.create(:notificar_email => 'dobry@adres.com')
    assert(i3.errors.messages[:notificar_email].nil?)
  end

end
